import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Arrays;

public class ImageProcess {
    public static void main(String args[]){
       
        File file  = new File("eyeM.bmp");
        try {
            BufferedImage RGB_img = ImageIO.read(file);
            int height = RGB_img.getHeight();
            int width = RGB_img.getWidth();
            
            int [] gray_img = new int[width * height];
            gray_img = Gray(RGB_img);
            Negative(gray_img, width, height);
            int [] gamma_img1 = new int[width * height];
            int [] gamma_img2 = new int[width * height];
            int [] gamma_img3 = new int[width * height];
            gamma_img1 = Gamma(gray_img, width, height, 0.5);
            gamma_img2 = Gamma(gray_img, width, height, 1);
            gamma_img3 = Gamma(gray_img, width, height, 2);


            int [] peper_img = new int[width * height];
            peper_img = Pepper(gamma_img1, width, height);

            Median(peper_img, width, height);

            Mean(peper_img, width, height);

            Min(peper_img, width, height);

            Max(peper_img, width, height);

            Laplacian(gamma_img3, width, height);

            Sobel(gamma_img2, width, height);

            OTSU(gamma_img3, width, height);
            
        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    public static int[] Gray(BufferedImage RGB_img){
        int width = RGB_img.getWidth();
        int height = RGB_img.getHeight();
        int [] pixels = new int[width * height];
        int [] pixels_result = new int[width * height];


        RGB_img.getRGB(0, 0, width, height, pixels, 0, width);

        for(int i = 0; i < width*height ; i++){
            int rgb = pixels[i];
            int red = (rgb & 0xff0000) >> 16;
            int green= (rgb & 0x00ff00) >> 8;
            int blue= rgb & 0x0000ff;
            int gray = (int)(0.299 * red + 0.587 * green + 0.114 * blue);
            pixels[i] = (0xff000000 | gray<<16 | gray<<8 | gray);
            pixels_result[i] = gray; 
        }

        BufferedImage gray_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        gray_image.setRGB(0, 0, width, height, pixels, 0, width);
        try {
            File file_gray = new File("gray.bmp");
            ImageIO.write(gray_image, "bmp", file_gray);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return pixels_result;
    }
    public static void Negative(int[] gray_img, int width, int height){
        int [] pixels = new int[gray_img.length];

        for(int i = 0; i < gray_img.length; i++){

            int negative = 255 - gray_img[i];
            pixels[i] = (0xff000000 | negative<<16 | negative<<8 | negative);
        }

        BufferedImage negative_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        negative_image.setRGB(0, 0, width, height, pixels, 0, width);

        try {
            File file_negative = new File("negative.bmp");
            ImageIO.write(negative_image, "bmp", file_negative);
        }catch (Exception e) {}
    }

    public static int[] Gamma(int [] gray_img, int width, int height, double gamma_value){
        int max = -1;
        int min = 256;
        int [] pixels = new int[gray_img.length];
        int [] pixels_result = new int[gray_img.length];

        for(int i = 0; i < gray_img.length; i++){
            if(gray_img[i] > max){
                max = gray_img[i];
            }
            if(gray_img[i] < min){
                min = gray_img[i];
            }
        }


        for(int i = 0; i < gray_img.length; i++){
            double gamma_double = Math.pow((double)(gray_img[i] - min)/(max - min), gamma_value)*255;
            int gamma = (int) gamma_double;
            pixels[i] = (0xff000000 | gamma << 16 | gamma << 8 | gamma);
            pixels_result[i] = gamma;
        }

        BufferedImage gama_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        gama_image.setRGB(0, 0, width, height, pixels, 0, width);

        try {
            File file_gama = new File("gamma_" + gamma_value + ".bmp");
            ImageIO.write(gama_image, "bmp", file_gama);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return pixels_result;
    }


    public static int[] Pepper(int [] gamma_img, int width, int height){
        int [] pixels = new int[gamma_img.length];
        int [] pepper_result = new int[gamma_img.length];

        for(int i = 0; i < gamma_img.length; i++){
            pepper_result[i] = gamma_img[i];
        }

        Random random = new Random();
        for(int i = 0; i < gamma_img.length; i++){

            int pepper = random.nextInt(10);

            if(pepper == 9){
                pepper_result[i]=0;
            }
            else if(pepper == 0){
                pepper_result[i]=255;
            }
            pixels[i] = (0xff000000 | pepper_result[i] << 16 | pepper_result[i] << 8 | pepper_result[i]);
        }

        BufferedImage pepper_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        pepper_image.setRGB(0, 0, width, height, pixels, 0, width);

        try {
            File file_pepper = new File("peper.bmp");
            ImageIO.write(pepper_image, "bmp", file_pepper);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return pepper_result;
    }

    public static void Median(int [] peper_img, int width, int height){
        int [] pixels = new int[peper_img.length];
        int [] median_result = new int[peper_img.length];

        for(int i = 0; i < peper_img.length; i++){
            median_result[i] = peper_img[i];
        }

        for(int i = 1; i < width - 1; i++){
            for(int j = 1; j < height - 1 ; j++){
                int [] window = new int[9];

                window[0]=peper_img[width*j + i - width - 1]; 
                window[1]=peper_img[width*j + i - width]; 
                window[2]=peper_img[width*j + i - width + 1];
                window[3]=peper_img[width*j + i - 1]; 
                window[4]=peper_img[width*j + i]; 
                window[5]=peper_img[width*j + i + 1];
                window[6]=peper_img[width*j + i + width - 1];
                window[7]=peper_img[width*j + i + width];
                window[8]=peper_img[width*j + i + width + 1];

                Arrays.sort(window);
                median_result[width*j + i] = window[4];
            }
        }

        for(int i = 0; i < peper_img.length; i++){
            pixels[i] = (0xff000000 | median_result[i] << 16 | median_result[i] << 8 | median_result[i]);
        }

        BufferedImage median_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        median_image.setRGB(0, 0, width, height, pixels, 0, width);

        try {
            File file_median = new File("median.bmp");
            ImageIO.write(median_image, "bmp", file_median);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Mean(int [] peper_img, int width, int height){
    	int mean_result[]=new int[peper_img.length]; 


        for(int i = 1; i < width - 1; i++){
            for(int j = 1; j < height - 1; j++){
                int window[]=new int[9];

                window[0]=peper_img[width*j + i - width - 1]; 
                window[1]=peper_img[width*j + i - width]; 
                window[2]=peper_img[width*j + i - width + 1]; 
                window[3]=peper_img[width*j + i - 1];
                window[4]=peper_img[width*j + i]; 
                window[5]=peper_img[width*j + i + 1];
                window[6]=peper_img[width*j + i + width - 1];
                window[7]=peper_img[width*j + i + width];
                window[8]=peper_img[width*j + i + width + 1]; 

                int total = 0;
                for(int k = 0; k < 9; k++) {
                    total += window[k];
                }

                int average = total / 9;
                mean_result[width*j + i] = average;
            }
        }

        for(int i = 0; i < peper_img.length; i++){
            mean_result[i] = (0xff000000 | mean_result[i] << 16 | mean_result[i] << 8 | mean_result[i]);
        }

        BufferedImage mean_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        mean_image.setRGB(0, 0, width, height, mean_result, 0, width);

        try {
            File file_mean = new File("mean.bmp");
            ImageIO.write(mean_image, "bmp", file_mean);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    public static void Min(int [] peper_img, int width, int height){
        int min_result[]= new int[peper_img.length];
       

        for(int i = 1; i < width - 1; i++){
            for(int j = 1; j < height - 1; j++){
                int window[]=new int[9];

                window[0]=peper_img[width*j + i - width - 1];
                window[1]=peper_img[width*j + i - width]; 
                window[2]=peper_img[width*j + i - width + 1]; 
                window[3]=peper_img[width*j + i - 1]; 
                window[4]=peper_img[width*j + i]; 
                window[5]=peper_img[width*j + i + 1];
                window[6]=peper_img[width*j + i + width - 1]; 
                window[7]=peper_img[width*j + i + width];
                window[8]=peper_img[width*j + i + width + 1];

                Arrays.sort(window);
                min_result[width*j + i] = window[0];
            }
        }

        for(int i = 0; i < peper_img.length; i++){
            min_result[i] = (0xff000000 | min_result[i] << 16 | min_result[i] << 8 | min_result[i]);
        }

        BufferedImage min_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); 
        min_image.setRGB(0, 0, width, height, min_result, 0, width);

        try {
            File file_min = new File("min.bmp");
            ImageIO.write(min_image, "bmp", file_min);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void Max(int [] peper_img, int width, int height){
        int max_result[]= new int[peper_img.length];
       

        for(int i = 1; i < width - 1; i++){
            for(int j = 1; j < height - 1; j++){
                int window[]=new int[9];

                window[0]=peper_img[width*j + i - width - 1];
                window[1]=peper_img[width*j + i - width]; 
                window[2]=peper_img[width*j + i - width + 1]; 
                window[3]=peper_img[width*j + i - 1]; 
                window[4]=peper_img[width*j + i]; 
                window[5]=peper_img[width*j + i + 1];
                window[6]=peper_img[width*j + i + width - 1];
                window[7]=peper_img[width*j + i + width];
                window[8]=peper_img[width*j + i + width + 1];

                Arrays.sort(window);
                max_result[width*j + i] = window[8];
            }
        }

        for(int i = 0; i < peper_img.length; i++){
            max_result[i] = (0xff000000 | max_result[i] << 16 | max_result[i] << 8 | max_result[i]);
        }

        BufferedImage max_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        max_image.setRGB(0, 0, width, height, max_result, 0, width);

        try {
            File file_max = new File("max.bmp");
            ImageIO.write(max_image, "bmp", file_max);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void Laplacian(int [] peper_img, int width, int height){
        
         int Laplacian_result[]= new int[peper_img.length];
        
         for(int i = 1; i < width - 1; i++){
             for(int j = 1; j < height - 1; j++){
                 int window[]=new int[9];
                 
                 window[0]=peper_img[width*j + i - width - 1]*0; 
                 window[1]=peper_img[width*j + i - width]*-1;
                 window[2]=peper_img[width*j + i - width + 1]*0; 
                 window[3]=peper_img[width*j + i - 1]*-1; 
                 window[4]=peper_img[width*j + i]*4; 
                 window[5]=peper_img[width*j + i + 1]*-1; 
                 window[6]=peper_img[width*j + i + width - 1]*0; 
                 window[7]=peper_img[width*j + i + width]*-1; 
                 window[8]=peper_img[width*j + i + width + 1]*0; 

                 
                 Laplacian_result[width*j + i] =window[1]+window[3]+window[4]+window[5]+window[7] ; 
             }
         }

         for(int i = 0; i < peper_img.length; i++){
        	 Laplacian_result[i] = (0xff000000 | Laplacian_result[i] << 16 | Laplacian_result[i] << 8 | Laplacian_result[i]);
         }

         BufferedImage Laplacian_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
         Laplacian_image.setRGB(0, 0, width, height, Laplacian_result, 0, width);

         try {
             File file_Laplacian = new File("Laplacian.bmp");
             ImageIO.write(Laplacian_image, "bmp", file_Laplacian);
         }catch (Exception e) {
             e.printStackTrace();
         }
     }
    
    public static void OTSU(int [] gamma_img, int width, int height){
        int threshold = 0;
        int total = 0;
        int [] pixels = new int[width * height];
        int [] otsu_result = new int[width * height];

        for (int i = 0; i < gamma_img.length ;i++){
            otsu_result[i] = gamma_img[i];
        }

        for(int i = 0; i < gamma_img.length ;i++){
            total += gamma_img[i];
        }

        threshold = total / (width*height);

        for(int i = 0; i < gamma_img.length; i++){
            if(gamma_img[i] >= threshold)
                otsu_result[i] = 255;
            else
                otsu_result[i] = 0;

            pixels[i] = (0xff000000 | otsu_result[i] << 16 | otsu_result[i] << 8 | otsu_result[i]);
        }

        BufferedImage otsu_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); 
        otsu_image.setRGB(0, 0, width, height, pixels, 0, width);

        try {
            File file_otsu = new File("otsu.bmp");
            ImageIO.write(otsu_image, "bmp", file_otsu);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Sobel(int [] gamma_img, int width, int height){
        int [] pixels = new int[gamma_img.length];
        int [][] Gx = new int[height][width];
        int [][] Gy = new int[height][width];
        int [][] G = new int[height][width];

        int [][] original = new int[height][width];
        int count = 0;
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                original[i][j] = gamma_img[count];
                count ++;
            }
        }

        int count2= 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i == 0 || i == height - 1 || j == 0 || j == width - 1)
                    Gx[i][j] = Gy[i][j] = G[i][j] = 0; 
                else {
                    Gy[i][j] =
                        original[i+1][j-1] + 2*original[i+1][j] + original[i+1][j+1] -
                        original[i-1][j-1] - 2*original[i-1][j] - original[i-1][j+1];
                    Gx[i][j] =
                        original[i-1][j+1] + 2*original[i][j+1] + original[i+1][j+1] -
                        original[i-1][j-1] - 2*original[i][j-1] - original[i+1][j-1];
                    G[i][j] =(int)(Math.sqrt((Math.pow(Gx[i][j],2) + Math.pow(Gy[i][j],2))));
                }
                pixels[count2] = (0xff000000 | G[i][j] << 16 | G[i][j] << 8 | G[i][j]);
                count2 ++;
            }
        }

        BufferedImage sobel_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        sobel_image.setRGB(0, 0, width, height, pixels, 0, width);

        try {
            File file_sobel = new File("sobel.bmp");
            ImageIO.write(sobel_image, "bmp", file_sobel);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
