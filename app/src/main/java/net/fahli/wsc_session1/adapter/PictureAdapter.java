package net.fahli.wsc_session1.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import net.fahli.wsc_session1.R;
import net.fahli.wsc_session1.entity.Picture;
import net.fahli.wsc_session1.helper.HttpConnection;
import net.fahli.wsc_session1.helper.PictureCallback;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class PictureAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Picture> pictures;
    private PictureCallback callback;
    public PictureAdapter(Context context, ArrayList<Picture> pictures, PictureCallback callback) {
        this.context = context;
        this.pictures = pictures;
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return pictures.size();
    }

    @Override
    public Object getItem(int position) {
        return pictures.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_picture, parent, false);
        ImageView imageView = convertView.findViewById(R.id.imgView);
        TextView txtPic = convertView.findViewById(R.id.txtPic);
        int pic = position + 1;
        txtPic.setText("Picture " + String.valueOf(pic));
        ImageView btnRemove = convertView.findViewById(R.id.btnRemove);

        final Picture item = pictures.get(position);
        if (item.getType().equals("Local")) {
            imageView.setImageBitmap(item.getBitmap());
        } else {
            LoadImage loadImage = new LoadImage(imageView);
            loadImage.execute("http://10.0.2.2/wsc/session1/view_image.php?id=" + item.getId());
        }

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onRemoveClicked(item);
            }
        });

        return convertView;
    }

    private class LoadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView imgView;

        public LoadImage(ImageView imgView) {
            this.imgView = imgView;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imgView.setImageBitmap(bitmap);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            String url = strings[0];
            try {
                //InputStream inputStream = new URL(url).openStream();
                //bitmap = BitmapFactory.decodeStream(inputStream);
                HttpConnection httpConnection = new HttpConnection();
                String pic = httpConnection.Connect(url);

                byte[] image = Base64.decode(pic, Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }
}
