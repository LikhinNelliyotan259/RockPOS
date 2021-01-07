package com.rockchipme.app.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rockchipme.app.R;
import com.rockchipme.app.adapters.ThumbImagesAdapter;
import com.rockchipme.app.helpers.ItemClickSupport;
import com.rockchipme.app.models.Thumb;
import com.rockchipme.app.widgets.TouchImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageViewerActivity extends AppCompatActivity {
    
    ViewPager pager;
    ArrayList<String> arrayImages;
    private GalleryPagerAdapter adapter;
    int currIndex = 0;
    List<Thumb> arrayThumbs;
    RecyclerView rv;
    ThumbImagesAdapter adapterThumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialising widgets
        initialiseViews();

        Bundle extras = getIntent().getExtras();
        arrayImages = extras.getStringArrayList("images");
        String position = extras.getString("position");
        if(position == null){
            position = "0";
        }
        // Pager Adapter
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new GalleryPagerAdapter(ImageViewerActivity.this, arrayImages);
        pager.setAdapter(adapter);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //indicator.setCurrentItem(position);
            }

            @Override
            public void onPageSelected(int position) {
                currIndex = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        arrayThumbs = new ArrayList<>();
        for(String image:arrayImages){
            arrayThumbs.add(new Thumb(image, false));
        }
        arrayThumbs.get(Integer.parseInt(position)).setSelected(true);
        rv = (RecyclerView) findViewById(R.id.rv_images);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterThumb = new ThumbImagesAdapter(getApplicationContext(), arrayThumbs);
        rv.setAdapter(adapterThumb);

        ItemClickSupport.addTo(rv).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                //Toast.makeText(ProductImagePopupActivity.this, ""+arrayImages.get(position), Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(getApplicationContext(), ItemDetailsActivity.class));
                if(pager.getCurrentItem() != position){
//                    for(int i=0;i<arrayThumbs.size();i++){
//                        arrayThumbs.get(i).setSelected(false);
//                    }
//                    arrayThumbs.get(position).setSelected(true);
//                    adapterThumb.notifyDataSetChanged();
                    pager.setCurrentItem(position);
                }
            }
        });

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                // Check if this is the page you want.
                for(int i=0;i<arrayThumbs.size();i++){
                    arrayThumbs.get(i).setSelected(false);
                }
                arrayThumbs.get(position).setSelected(true);
                pager.setCurrentItem(position);
                adapterThumb.notifyDataSetChanged();
            }
        });
        if(Integer.parseInt(position) != 0){
            pager.setCurrentItem(Integer.parseInt(position));
        }
    }

    private void initialiseViews() {
    }

    // Gallery adapter
    class GalleryPagerAdapter extends PagerAdapter {

        Context context;
        LayoutInflater inflater;
        ArrayList<String> image;

        public GalleryPagerAdapter(Context context, ArrayList<String> list_images) {
            context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.image = list_images;
        }

        @Override
        public int getCount() {

            return image.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            // Fullscreen image --> pager item
            View itemView = inflater.inflate(R.layout.pager_gallery_item, container, false);
            container.addView(itemView);

           /* final SubsamplingScaleImageView imageView =
                    (SubsamplingScaleImageView) itemView.findViewById(R.id.image);
            Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.login_logo);
            imageView.setImage(ImageSource.bitmap(icon));

            // Asynchronously load the image and set the thumbnail and pager view
            Glide.with(_context)
                    .load(Constants.BASE_URL+arrayImages.get(position))
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            imageView.setImage(ImageSource.bitmap(bitmap));
                        }
                    });
*/
            final TouchImageView imageView = (TouchImageView) itemView.findViewById(R.id.image);
//            final ImageView imageView = (ImageView) itemView.findViewById(R.id.image);
            Picasso.with(context)
                    .load(arrayImages.get(position))
                    .resize(1500, 1500)
                    .centerInside()
                    .into(imageView);
//            Glide.with(_context)
//                    .load(arrayImages.get(position))
//                    .asBitmap()
//                    .placeholder(R.drawable.login_logo)
//                    .fitCenter()
//                    .into(imageView);
            //imageView.setZoom(.65f);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}