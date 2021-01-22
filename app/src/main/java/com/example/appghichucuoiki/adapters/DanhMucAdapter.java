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
import com.example.bigexcersice.adapters.event.OnDanhMucItemListener;
import com.example.bigexcersice.model.CongViec;
import com.example.bigexcersice.model.DanhMuc;

import java.util.ArrayList;
import java.util.List;

public class DanhMucAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private List<DanhMuc> listDanhMuc;
    private int layout;
    private OnDanhMucItemListener onDanhMucItemListener;
    private ItemFilter mFilter = new ItemFilter();
    private List<DanhMuc>originalData = null;

    public DanhMucAdapter(Context context, List<DanhMuc> listDanhMuc, int layout, OnDanhMucItemListener onDanhMucItemListener) {
        this.context = context;
        this.listDanhMuc = listDanhMuc;
        this.layout = layout;
        this.onDanhMucItemListener = onDanhMucItemListener;
        this.originalData = listDanhMuc;
    }

    @Override
    public int getCount() {
        return listDanhMuc.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    public class ViewHolder{
        TextView txtTieuDe, txtNgayTao;
        ImageView imgMore, imgDelete;
    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if(view == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);
            holder.txtTieuDe = view.findViewById(R.id.id_tenDM_txt);
            holder.txtNgayTao = view.findViewById(R.id.id_ngayTao_DM_txt);
            holder.imgMore = view.findViewById(R.id.id_moreInfo_DM_imgView);
            holder.imgDelete = view.findViewById(R.id.id_deleteDM_imgView);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        DanhMuc dm = listDanhMuc.get(position);
        holder.txtNgayTao.setText(dm.getNgayTao());
        holder.txtTieuDe.setText(dm.getTenDanhMuc());

        holder.imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDanhMucItemListener.onButtonLoadMore(v,position,listDanhMuc.get(position).getId(), listDanhMuc.get(position).getTenDanhMuc());
            }
        });
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDanhMucItemListener.onButtonDeleteDM(position,listDanhMuc.get(position).getId());
            }
        });
        return view;
    }
    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<DanhMuc> list = originalData;

            int count = list.size();
            final ArrayList<DanhMuc> nlist = new ArrayList<DanhMuc>(count);

            DanhMuc filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.getTenDanhMuc().toLowerCase().contains(filterString) || filterableString.getNgayTao().contains(filterString)) {
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
            listDanhMuc = (ArrayList<DanhMuc>) results.values;
            notifyDataSetChanged();
        }
    }
}
