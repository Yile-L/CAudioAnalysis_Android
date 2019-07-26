import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class LoudnessPerFrame {
    int frameSize;
    String FilePath;
    List<Short> audioData;
    List<Double> result = new ArrayList<Double>();

    public LoudnessPerFrame(String Path){
        FilePath = Path;
        audioData = null;
        frameSize = 4410; //100ms
    }

    public LoudnessPerFrame(List<Short> Data){
        this(Data, 4410);
    }

    public LoudnessPerFrame(List<Short> Data, int sampelsPerFrame){
        audioData = Data;
        FilePath = null;
        frameSize = sampelsPerFrame;
    }

    public void Process(){
        if (FilePath != null) {
            try {
                File newFile = new File(FilePath);

                byte[] buffer = new byte[frameSize * 4];
                byte[] fileheader = new byte[44];

                FileInputStream inStream = new FileInputStream(newFile);

                int nRead;
                double[] floatbuffer;

                inStream.read(fileheader);

                while ((nRead = inStream.read(buffer)) != -1) {
                    floatbuffer = (new FloatBufferConverter(buffer)).result;
                    double amplitude = RMS(floatbuffer);
                    System.out.println(amplitude);
                    double dB = 20 * Math.log10(amplitude);
                    result.add(dB);
                }
            } catch (Exception e) {e.printStackTrace(); }
        } else{
            List<Short> temp = audioData;
            while (temp.size() > 0) {
                if (temp.size() < frameSize){
                    break;
                }
                List<Short> singleframe = temp.subList(0, frameSize);

                temp = temp.subList(frameSize, temp.size());
                double[] floatbuffer = (new FloatBufferConverter(singleframe)).result;
                double amplitude = RMS(floatbuffer);
                double dB = 20 * Math.log10(amplitude);
                result.add(dB);
            }
        }
    }

    public double RMS(double[] buffer){
        double sum = 0;
        for (double e : buffer){
            sum += e * e;
        }
        sum = sum / frameSize;

        return Math.sqrt(sum);
    }



}
