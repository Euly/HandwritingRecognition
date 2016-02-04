package unimi.sighinolfi.diana.handwritingrecognition;

import android.graphics.*;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Antonio on 03/02/2016.
 */
public class FreemanChainCode {
    private int color = Color.WHITE;
    int y=0,x=0;
    Bitmap bitmap;
    Point[] sequence={new Point(1,0),new Point(1,-1),new Point(0,-1),new Point(-1,-1),new Point(-1,0),new Point(-1,1),new Point(0,1),new Point(1,1)};
    HashMap<Point, Pixelinfo> imageinfo=new HashMap<>();
    int totalWhitePixel=0;
    int whitePixel=0;

    public void CalculateFreemanChainCode(Bitmap bitmap){

        for(int i=0;i<bitmap.getWidth();i++){
            for(int y=0;y<bitmap.getHeight();y++){
                if(isWhite(i,y))
                    totalWhitePixel++;
            }
        }
        y=bitmap.getHeight()-1;//mi posiziono sull'ultima riga
        while(bitmap.getPixel(x,y)!=color && x<=bitmap.getWidth())
            x++;

        if(x==bitmap.getWidth())
            return;

        while(whitePixel!=totalWhitePixel) {
            getNextPositions();
            getNextPixel();
        }
    }


    public void getNextPositions(){
        int[] directions=new int[8];
        int indexDirections=0;
        Pixelinfo currentPixel=imageinfo.get(new Point(x,y));
        if(!currentPixel.visited) {
            for (int i = 0; i < sequence.length; i++) {
                if (isWhite(x + sequence[i].x, y + sequence[i].y)) {
                    //aggiungi alla lista di posizioni possibili
                    directions[indexDirections++] = i;
                    imageinfo.get(new Point(x + sequence[i].x, y + sequence[i].y)).isWhite = true;
                    whitePixel++;
                }
            }
        }
        currentPixel.directions=directions;
        currentPixel.directionsAvaiable=indexDirections;
        currentPixel.visited=true;
    }

    public void getNextPixel(){
        //prendo il primo pixel che non è stato visitato, se non c'è nè, prendo il primo
        Point currentPoint=new Point(x,y);
        Pixelinfo currentPixel=imageinfo.get(currentPoint);
        for(int direction=0;direction<currentPixel.directions.length;direction++){
            Point nextPixel=new Point(x+sequence[direction].x,y+sequence[direction].y);
            if(!imageinfo.get(nextPixel).visited) {
                //se non visitato, prendolo come prossimo
                x=nextPixel.x;
                y=nextPixel.y;
                currentPixel.nextDirection=direction;
                break;
            }
        }

        if(x==currentPoint.x && y==currentPoint.y){//se sono tutti visistati, prendo il primo
            Point nextPixel=new Point(x+sequence[0].x,y+sequence[0].y);
                x=nextPixel.x;
                y=nextPixel.y;
        }
    }

    public boolean isWhite(int x,int y){
        if(x<0 || x>bitmap.getWidth())
            return false;
        if(y<0 || y>bitmap.getHeight())
            return false;
        if(bitmap.getPixel(x,y)==color)
            return true;
        return false;
    }


    class Pixelinfo{
        boolean visited=false;
        int[] directions=new int[8];
        int directionsAvaiable=0;
        boolean isWhite=false;
        int nextDirection=-1;//prossima direzione dal pixel
    }
}
