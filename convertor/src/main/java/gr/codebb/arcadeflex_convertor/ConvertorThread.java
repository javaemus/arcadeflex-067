package gr.codebb.arcadeflex_convertor;

public class ConvertorThread extends Thread{
     @Override
    public void run() {
         new Convertor();
     }
    
}
