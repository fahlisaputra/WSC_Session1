package net.fahli.wsc_session1.adapter;

import android.content.Context;
import android.content.Intent;
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

import net.fahli.wsc_session1.AssetEditorActivity;
import net.fahli.wsc_session1.AssetTransferActivity;
import net.fahli.wsc_session1.R;
import net.fahli.wsc_session1.TransferHistoryActivity;
import net.fahli.wsc_session1.entity.Catalogue;
import net.fahli.wsc_session1.helper.HttpConnection;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Catalogue> catalogues = new ArrayList<>();

    public void setCatalogues(ArrayList<Catalogue> catalogues) {
        this.catalogues = catalogues;
    }

    public ListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return catalogues.size();
    }

    @Override
    public Object getItem(int position) {
        return catalogues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }
        ImageView imgView = itemView.findViewById(R.id.img_logo);
        ImageView btnEdit = itemView.findViewById(R.id.btn_edit);
        ImageView btnHistory = itemView.findViewById(R.id.btn_history);
        ImageView btnTransfer = itemView.findViewById(R.id.btn_transfer);

        final Catalogue catalogue = (Catalogue) getItem(position);
        TextView asset_name, asset_sn, department_name;
        asset_name = itemView.findViewById(R.id.asset_name);
        asset_sn = itemView.findViewById(R.id.asset_sn);
        department_name = itemView.findViewById(R.id.department_name);

        asset_name.setText(catalogue.getAssetName());
        asset_sn.setText(catalogue.getAssetSN());
        department_name.setText(catalogue.getDepartmentName());

        ArrayList<String> photos  = catalogue.getPhotos();
        if (photos.size() != 0) {
            LoadImage loadImage = new LoadImage(imgView);
            loadImage.execute("http://10.0.2.2/wsc/session1/view_image.php?id=" + photos.get(0));
        }

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent history = new Intent(context, TransferHistoryActivity.class);
                history.putExtra(TransferHistoryActivity.EXTRA_ID, catalogue.getID());
                context.startActivity(history);
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit = new Intent(context, AssetEditorActivity.class);
                edit.putExtra(AssetEditorActivity.EXTRA_ASSET, catalogue);
                context.startActivity(edit);
                notifyDataSetChanged();
            }
        });

        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent transfer = new Intent(context, AssetTransferActivity.class);
                transfer.putExtra(AssetTransferActivity.EXTRA_ASSET, catalogue);
                context.startActivity(transfer);
            }
        });
        return itemView;
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
