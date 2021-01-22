package com.example.appghichucuoiki.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bigexcersice.R;
import com.example.bigexcersice.adapters.event.OnGhiChuItemTouchListener;
import com.example.bigexcersice.model.CongViec;

import java.util.ArrayList;
import java.util.List;

public class CongViecAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private int layout;
    private List<CongViec> congViecList;
    private OnGhiChuItemTouchListener onGhiChuItemTouchListener;
    private ItemFilter mFilter = new ItemFilter();
    private List<CongViec>originalData = null;

    public CongViecAdapter(Context context, int layout, List<CongViec> congViecList, OnGhiChuItemTouchListener onGhiChuItemTouchListener) {
        this.context = context;
        this.layout = layout;
        this.congViecList = congViecList;
        this.onGhiChuItemTouchListener = onGhiChuItemTouchListener;
        this.originalData = congViecList ;
    }

    @Override
    public int getCount() {
        return congViecList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public Filter getFilter() {
        return mFilter;
    }


    private class ViewHolder{
        TextView txtTen, txtNgayTao;
        ImageView imgDelete, imgEdit, imgLoadMore;
    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if(view==null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);
            holder.txtTen = view.findViewById(R.id.id_tenCV_txt);
            holder.txtNgayTao = view.findViewById(R.id.id_ngayTao_txt);

            holder.imgDelete = view.findViewById(R.id.id_delete_imgView);
            holder.imgEdit = view.findViewById(R.id.id_edit_imgView);
            holder.imgLoadMore = view.findViewById(R.id.id_moreInfo_imgView);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        CongViec cv = congViecList.get(position);
        holder.txtTen.setText(cv.getTenCV());

        holder.txtNgayTao.setText(cv.getNgayTao());

        // bắt sự kiện xoá - sửa
        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ghiChuID = congViecList.get(position).getIdCV();
                onGhiChuItemTouchListener.onButtonEditGhiChu(v,position,ghiChuID);
            }
        });
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ghiChuID = congViecList.get(position).getIdCV();
                onGhiChuItemTouchListener.onButtonDeleteGhiChu(v,position,ghiChuID);
            }
        });
        holder.imgLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ghiChuID = congViecList.get(position).getIdCV();
                onGhiChuItemTouchListener.onButtonLoadMore(v,position,ghiChuID);
            }
        });
        return view;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<CongViec> list = originalData;

            int count = list.size();
            final ArrayList<CongViec> nlist = new ArrayList<CongViec>(count);

            CongViec filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.getTenCV().toLowerCase().contains(filterString) || filterableString.getNgayTao().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            congViecList = (ArrayList<CongViec>) results.values;
            notifyDataSetChanged();
        }

    }
}
