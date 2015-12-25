package com.example.jccartoondemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

/**
 * Created by 潘建成 on 2015/12/24.
 */
public class CartoonUtil {

    public static Bitmap getCartoonBitmap(String path, int width){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);
        Bitmap bit = BitmapFactory.decodeFile(path);
        int width_jpg = bit.getWidth();
        int height_jpg = bit.getHeight();
        float scale = (float) (width*1.0/width_jpg*1.0);
        Matrix matrix = new Matrix();
        matrix.postScale(scale,scale);
        Bitmap bitmap = Bitmap.createBitmap(bit,0,0,width_jpg,height_jpg,matrix,true);
        return bitmap;
    }
}
