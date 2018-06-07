package tech.peller.mrblackandroidwatch.utils.imageSlider;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import roboguice.fragment.provided.RoboFragment;
import roboguice.inject.InjectView;
import tech.peller.mrblackandroidwatch.R;
import tech.peller.mrblackandroidwatch.models.venue.ImageWithTitle;

/**
 * Created by Sam (samir@peller.tech) on 21.04.2017
 */

public class SliderElementFragment extends RoboFragment implements View.OnTouchListener {
    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;

    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    int mode = NONE;
    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    @InjectView(R.id.elementImage)
    private ImageView elementImage;
    private ImageWithTitle image;

    public SliderElementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_slider_element, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Drawable drawable = ContextCompat.getDrawable(getActivity(),
                R.drawable.ava2x);
        Picasso.with(getActivity()).load(image.getUrl()).error(drawable)
                .placeholder(drawable).into(elementImage);

        elementImage.setOnTouchListener(this);
    }

    public void setImage(ImageWithTitle image) {
        this.image = image;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(elementImage.getScaleType() != ImageView.ScaleType.MATRIX) {
            WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            Drawable drawable = elementImage.getDrawable();
            if (drawable == null) return false;
            int bitmapWidth = drawable.getIntrinsicWidth(); //this is the bitmap's width
            int bitmapHeight = drawable.getIntrinsicHeight(); //this is the bitmap's height

            float scaledRate = (float) size.x / bitmapWidth;
            float viewCenterY = (float) elementImage.getHeight() / 2 - bitmapHeight * scaledRate / 2;

            //матрица скаллирования - сначала изменяем размеры. Для равномерного изменения - числа
            // должны быть одинаковыми. Потом двигаем в цетр экрана, чтобы визуальных изменений не было
            matrix.setScale(scaledRate, scaledRate);
            matrix.postTranslate(0f, viewCenterY);

            elementImage.setScaleType(ImageView.ScaleType.MATRIX);
        } else {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    savedMatrix.set(matrix);
                    start.set(event.getX(), event.getY());
                    mode = DRAG;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        matrix.set(savedMatrix);
                        matrix.postTranslate(event.getX() - start.x, event.getY()
                                - start.y);
                    } else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        if (newDist > 10f) {
                            matrix.set(savedMatrix);
                            float scale = newDist / oldDist;
                            matrix.postScale(scale, scale, mid.x, mid.y);
                        }
                    }
                    break;
            }
        }

        elementImage.setImageMatrix(matrix);
        return true;
    }

    /** Determine the space between the first two fingers */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}
