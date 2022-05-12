import com.musicg.api.DetectionApi;
import com.musicg.graphic.GraphicRender;
import com.musicg.wave.Wave;
import com.musicg.wave.extension.Spectrogram;
import org.jfree.chart.JFreeChart;
import org.jtransforms.fft.DoubleFFT_1D;
import org.jtransforms.fft.DoubleFFT_2D;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SplittableRandom;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {
        Wave wave = new Wave("D:/Test/sound.wav");
        System.out.println(wave.getLengthInSeconds());
        System.out.println(wave.getWaveHeader());
        double[] _amplitudes = wave.getNormalizedAmplitudes();

       printInfile(_amplitudes, "first.txt");

       double[] _amplitudesCopy  = Arrays.copyOfRange(_amplitudes, 0, 65536);
       DoubleFFT_1D _doubleFFT_1D = new DoubleFFT_1D(_amplitudesCopy.length);
       _doubleFFT_1D.realForward(_amplitudesCopy);

        double[] _fftReal = new double[_amplitudesCopy.length/2];
        double[] _fftIm = new double[_amplitudesCopy.length/2];
        int count = 0;
        for (int i = 0; i < _amplitudesCopy.length; i+=2) {
                _fftReal[count] = _amplitudesCopy[i];
                _fftIm[count] = _amplitudesCopy[i+1];
                count++;

        }

        double[] _magnitude = new double[_fftReal.length];
        double[] _phase = new double[_fftReal.length];
        for (int i = 0; i < _magnitude.length; i++) {
            _magnitude[i] = Math.sqrt(_fftReal[i]*_fftReal[i] + _fftIm[i]*_fftIm[i]);
            _phase[i] = Math.atan2(_fftIm[i], _fftReal[i]);
        }

      printInfile(_fftReal, "fftReal.txt");
      printInfile(_fftIm, "fftIm.txt");
      printInfile(_magnitude, "magnitude.txt");
      printInfile(_phase, "phase.txt");

    }

    private static <T>void printInfile(double[] array, String fileName) throws IOException {
        FileWriter _writer = new FileWriter(fileName);
        Arrays.stream(array).boxed()
                .map(String::valueOf)
                .limit(65536)
                .map(i -> i.replace(".", ","))
                .forEach(i -> {
            try {
                _writer.write(i + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        _writer.close();
    }

    private static double[] toDoubleArray(short[] target){
        double[] _result = new double[target.length];
        for (int i = 0; i < target.length; i++) {
            _result[i] = target[i];
        }
        return _result;
    }



}
