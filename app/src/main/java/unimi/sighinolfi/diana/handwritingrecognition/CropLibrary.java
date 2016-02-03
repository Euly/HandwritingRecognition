package unimi.sighinolfi.diana.handwritingrecognition;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Antonio on 02/02/2016.
 */
public class CropLibrary {

    private int cropTopY = 0;//up locked coordinate
    private int cropBottomY = 0;//down locked coordinate
    private int cropLeftX = 0;//left locked coordinate
    private int cropRightX = 0;//right locked coordinate
    private Bitmap imageWithChars = null;
    private boolean endOfImage;//end of picture
    private boolean endOfRow;//end of current reading row

    /**
     * This method scans image pixels until it finds the first black pixel (TODO: use foreground color which is black by default).
     * When it finds black pixel, it sets cropTopY and returns true. if it reaches end of image and does not find black pixels,
     * it sets endOfImage flag and returns false.
     * @return - returns true when black pixel is found and cropTopY value is changed, and false if cropTopY value is not changed
     */
    private boolean findCropTopY() {
        for (int y = cropBottomY; y < imageWithChars.getHeight(); y++) { // why cropYDown? - for multiple lines of text using cropBottomY from previous line above; for first line its zero
            for (int x = cropLeftX; x < imageWithChars.getWidth(); x++) { // scan starting from the previous left crop position - or it shoud be right???
                if (imageWithChars.getPixel(x, y) == Color.BLACK) { // if its black rixel (also consider condition close to black or not white or different from background)
                    this.cropTopY = y;   // save the current y coordiante
                    return true;        // and return true
                }
            }
        }
        endOfImage = true;  //sets this flag if no black pixels are found
        return false;       // and return false
    }

    /**
     * This method scans image pixels until it finds first row with white pixels. (TODO: background color which is white by default).
     * When it finds line whith all white pixels, it sets cropBottomY and returns true
     * @return - returns true when cropBottomY value is set, false otherwise
     */
    private boolean findCropBottomY() {
        for (int y = cropTopY + 1; y < imageWithChars.getHeight(); y++) { // scan image from top to bottom
            int whitePixCounter = 0; //counter of white pixels in a row
            for (int x = cropLeftX; x < imageWithChars.getWidth(); x++) { // scan all pixels to right starting from left crop position
                if (imageWithChars.getPixel(x, y) == -1) {    // if its white pixel
                    whitePixCounter++;                      // increase counter
                }
            }
            if (whitePixCounter == imageWithChars.getWidth()) { // if we have reached end of line counting white pixels (x pos)
                cropBottomY = y;                                // that means that we've found white line, so set current y coordinate minus 1
                return true;                                    // as cropBottomY and finnish with true
            }
            if (y == imageWithChars.getHeight() - 1) {  // if we have reached end of image
                cropBottomY = y;                        // set crop bottom
                endOfImage = true;                      // set corresponding endOfImage flag
                return true;                            // and return true
            }
        }
        return false;                                   // this should never happen, however its possible if image has non white bg
    }

    /**
     * This method scans image pixels to the right  until it finds first black pixel, or reach end of row.
     * When black pixel is found it sets cropLeftX and returns true. It should set cropLeftX to the next letter in a row.
     * @return - return true when true when black pixel is found and cropLeftX is changed, false otherwise
     */
    private boolean findCropLeftX() {
        int whitePixCounter = 0;                                            // white pixel counter between the letters
        for (int x = cropRightX; x < imageWithChars.getWidth(); x++) {      // start from previous righ crop position (previous letter), and scan following pixels to the right
            for (int y = cropTopY; y <= cropBottomY; y++) {             // vertical pixel scan at current x coordinate
                if (imageWithChars.getPixel(x, y) == Color.BLACK) {             // when we find black pixel
                    cropLeftX = x;                                          // set cropLeftX
                    return true;                                            // and return true
                }
            }

            // BUG?: this condition looks strange.... we might not need whitePixCounter at all, it might be used for 'I' letter
            whitePixCounter++;                                              // if its not black pixel assume that its white pixel
            if (whitePixCounter == 3) {                                     // why 3 pixels? its hard coded for some case and does not work in general...!!!
                whitePixCounter = 0;                                        // why does it sets to zero, this has no purporse at all...
            }
        }
        endOfRow = true;        // if we have reached end of row and we have not found black pixels, set the endOfRow flag
        return false;           // and return false
    }

    /**
     * This method scans image pixels to the right until it finds next row where all pixel are white, y1 and y2.
     * TODO: resiti broblem tolerancije n aboju bacgrounda kada ima prelaz...
     * @return - return true  when x2 value is changed and false when x2 value is not changed
     */
    private boolean findCropRightX() {
        for (int x = cropLeftX + 1; x < imageWithChars.getWidth(); x++) {   // start from current cropLeftX position and scan pixels to the right
            int whitePixCounter = 0;
            for (int y = cropTopY; y <= cropBottomY; y++) {             // vertical pixel scan at current x coordinate
                if (imageWithChars.getPixel(x, y) == -1) {                    // if we have white pixel at current (x, y)
                    whitePixCounter++;                                      // increase whitePixCounter
                }
            }

            // this is for space!
            int heightPixels = cropBottomY - cropTopY;                      // calculate crop height
            if (whitePixCounter == heightPixels+1) {                         // if white pixel count is equal to crop height+1  then this is white vertical line, means end of current char/ (+1 is for case when there is only 1 pixel; a 'W' bug fix)
                cropRightX = x;                                             // so set cropRightX
                return true;                                                // and return true
            }

            // why we need this when we allready have condiiton in the for loop? - for the last letter in the row.
            if (x == imageWithChars.getWidth() - 1) {                       // if we have reached end of row with x position
                cropRightX = x;                                             // set cropRightX
                endOfRow = true;                                            // set endOfRow flag
                return true;                                                // and return true
            }
        }

//        endOfRow = true;                                                    // da li ovo treba , nije ga bilo?
        return false;                                                       // return false if we have not found vertical white line (end of letter) to the right
    }

    public Bitmap extractCharImageToRecognize(Bitmap tocrop) {
        Bitmap trimedImages=null;
        int i = 0;

        while (endOfImage == false) {
            endOfRow = false;
            boolean foundTop = findCropTopY();
            boolean foundBottom = false;
            if (foundTop == true) {
                foundBottom = findCropBottomY();
                if (foundBottom == true) {
                    while (endOfRow == false) {
                        boolean foundLeft = false;
                        boolean foundRight = false;
                        foundLeft = findCropLeftX();
                        if (foundLeft == true) {
                            foundRight = findCropRightX();
                            if (foundRight == true) {
                                endOfImage=true;
                                Rect rect = new Rect(-50, -25, 150, 75);
                                new Rect(cropLeftX,cropTopY,cropRightX,cropBottomY);
//  Be sure that there is at least 1px to slice.
                                assert(rect.left < rect.right && rect.top < rect.bottom);
//  Create our resulting image (150--50),(75--25) = 200x100px
                                trimedImages = Bitmap.createBitmap(rect.right-rect.left, rect.bottom-rect.top, Bitmap.Config.ALPHA_8);
//  draw source bitmap into resulting image at given position
                            }
                        }
                    }
                    cropLeftX = 0;
                    cropRightX = 0;
                }
            }
        }
        cropTopY = 0;
        cropBottomY = 0;
        endOfImage = false;

        return trimedImages;
    }

    public Bitmap createContrast(Bitmap src, double value) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        int A, R, G, B;
        int pixel;
        double contrast = Math.pow((100 + value) / 100, 2);
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = Color.red(pixel);
                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.red(pixel);
                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        return bmOut;
    }

    public Bitmap ConvertToNegative(Bitmap sampleBitmap){
        ColorMatrix negativeMatrix =new ColorMatrix();
        float[] negMat={-1, 0, 0, 0, 255, 0, -1, 0, 0, 255, 0, 0, -1, 0, 255, 0, 0, 0, 1, 0 };
        negativeMatrix.set(negMat);
        final ColorMatrixColorFilter colorFilter= new ColorMatrixColorFilter(negativeMatrix);
        Bitmap rBitmap = sampleBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Paint paint=new Paint();
        paint.setColorFilter(colorFilter);
        Canvas myCanvas =new Canvas(rBitmap);
        myCanvas.drawBitmap(rBitmap, 0, 0, paint);
        return rBitmap;
    }
}
