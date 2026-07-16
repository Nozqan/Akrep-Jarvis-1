import {
  RecordingPresets,
  getRecordingPermissionsAsync,
  requestRecordingPermissionsAsync,
  setAudioModeAsync,
  useAudioRecorder,
  useAudioRecorderState,
} from "expo-audio";
import { useCallback, useEffect, useRef, useState } from "react";

const SESSIZLIK_ESIGI_DB = -46;
const KONUSMA_ESIGI_DB = -37;
const SESSIZLIK_SURESI_MS = 1200; // Sessizlik süresini biraz artırdık
const AZAMI_KAYIT_MS = 15_000; // Azami kayıt süresini 15 saniyeye çektik

export type SesOturumuDurumu = "hazir" | "izin-gerekli" | "kaydediyor" | "isleniyor" | "hata";

export type SesOturumuSonucu = {
  uri: string;
  sureMs: number;
};

type UseAkrepVoiceOptions = {
  onKayitTamamlandi?: (sonuc: SesOturumuSonucu) => void | Promise<void>;
};

const KAYIT_SECENEKLERI = {
  ...RecordingPresets.HIGH_QUALITY,
  isMeteringEnabled: true,
};

export function useAkrepVoice({ onKayitTamamlandi }: UseAkrepVoiceOptions = {}) {
  const recorder = useAudioRecorder(KAYIT_SECENEKLERI);
  const recorderState = useAudioRecorderState(recorder, 100);
  const [oturumDurumu, setOturumDurumu] = useState<SesOturumuDurumu>("hazir");
  const [hata, setHata] = useState<string | null>(null);
  const [sonKayitUri, setSonKayitUri] = useState<string | null>(null);
  
  const konusmaAlgilandiRef = useRef(false);
  const sessizlikBaslangiciRef = useRef<number | null>(null);
  const durduruluyorRef = useRef(false);
  const onKayitTamamlandiRef = useRef(onKayitTamamlandi);

  useEffect(() => {
    onKayitTamamlandiRef.current = onKayitTamamlandi;
  }, [onKayitTamamlandi]);

  const durdur = useCallback(async () => {
    if (durduruluyorRef.current) return;
    durduruluyorRef.current = true;
    setOturumDurumu("isleniyor");

    try {
      const sureMs = recorderState.durationMillis;
      await recorder.stop();
      const uri = recorder.uri;
      
      if (!uri) {
        throw new Error("Kayıt dosyası oluşturulamadı.");
      }

      setSonKayitUri(uri);
      // Callback'i beklemeden durumu güncellemek için await kullanmıyoruz veya 
      // callback içindeki hataların süreci tıkamasını engelliyoruz
      if (onKayitTamamlandiRef.current) {
        try {
          await onKayitTamamlandiRef.current({ uri, sureMs });
        } catch (e) {
          console.warn("Callback error:", e);
        }
      }
      
      setOturumDurumu("hazir");
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : "Ses kaydı durdurulamadı.";
      setHata(errorMessage);
      setOturumDurumu("hata");
      console.warn("Recording stop error:", error);
    } finally {
      durduruluyorRef.current = false;
      sessizlikBaslangiciRef.current = null;
      konusmaAlgilandiRef.current = false;
    }
  }, [recorder, recorderState.durationMillis]);

  // Otomatik durdurma mantığı
  useEffect(() => {
    if (!recorderState.isRecording || durduruluyorRef.current) return;

    // Azami süre aşımı
    if (recorderState.durationMillis >= AZAMI_KAYIT_MS) {
      void durdur();
      return;
    }

    const metering = recorderState.metering;
    if (typeof metering !== "number") return;

    // Konuşma algılama
    if (metering >= KONUSMA_ESIGI_DB) {
      konusmaAlgilandiRef.current = true;
      sessizlikBaslangiciRef.current = null;
    } 
    // Sessizlik algılama (sadece konuşma başladıktan sonra)
    else if (konusmaAlgilandiRef.current && metering <= SESSIZLIK_ESIGI_DB) {
      const simdi = Date.now();
      if (sessizlikBaslangiciRef.current === null) {
        sessizlikBaslangiciRef.current = simdi;
      } else if (simdi - sessizlikBaslangiciRef.current >= SESSIZLIK_SURESI_MS) {
        void durdur();
      }
    } else {
      sessizlikBaslangiciRef.current = null;
    }
  }, [durdur, recorderState.durationMillis, recorderState.isRecording, recorderState.metering]);

  const baslat = useCallback(async () => {
    if (recorderState.isRecording || durduruluyorRef.current) return true;
    setHata(null);

    try {
      let izin = await getRecordingPermissionsAsync();
      if (!izin.granted) izin = await requestRecordingPermissionsAsync();
      if (!izin.granted) {
        setOturumDurumu("izin-gerekli");
        setHata("Mikrofon izni verilmedi.");
        return false;
      }

      await setAudioModeAsync({
        playsInSilentMode: true,
        allowsRecording: true,
        interruptionMode: "doNotMix",
      });
      
      await recorder.prepareToRecordAsync();
      recorder.record();
      
      konusmaAlgilandiRef.current = false;
      sessizlikBaslangiciRef.current = null;
      setOturumDurumu("kaydediyor");
      return true;
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : "Ses kaydı başlatılamadı.";
      setHata(errorMessage);
      setOturumDurumu("hata");
      console.warn("Recording start error:", error);
      return false;
    }
  }, [recorder, recorderState.isRecording]);

  const birak = useCallback(() => {
    if (recorderState.isRecording && !durduruluyorRef.current) {
      void durdur();
    }
  }, [durdur, recorderState.isRecording]);

  const sifirla = useCallback(() => {
    setHata(null);
    setOturumDurumu("hazir");
  }, []);

  const normalizeGenlik = Math.max(0.18, Math.min(1, ((recorderState.metering ?? -60) + 60) / 42));

  return {
    baslat,
    birak,
    durdur,
    sifirla,
    oturumDurumu,
    kaydediyor: recorderState.isRecording,
    sureMs: recorderState.durationMillis,
    metering: recorderState.metering ?? null,
    normalizeGenlik,
    sonKayitUri,
    hata,
  };
}
