package rikka.akashitoolkit.fleet_editor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import rikka.akashitoolkit.R;
import rikka.akashitoolkit.adapter.BaseItemTouchHelperAdapter;
import rikka.akashitoolkit.adapter.SimpleAdapter;
import rikka.akashitoolkit.adapter.SimpleSelectableAdapter;
import rikka.akashitoolkit.model.Fleet;
import rikka.akashitoolkit.model.Ship;
import rikka.akashitoolkit.otto.BusProvider;
import rikka.akashitoolkit.otto.ItemSelectAction;
import rikka.akashitoolkit.staticdata.EquipTypeList;
import rikka.akashitoolkit.ui.widget.BottomSheetDialog;
import rikka.akashitoolkit.ui.widget.ListBottomSheetDialog;
import rikka.akashitoolkit.utils.Utils;
import rikka.akashitoolkit.viewholder.FleetEquipViewHolder;
import rikka.akashitoolkit.viewholder.FleetViewHolder;

/**
 * Created by Rikka on 2016/7/29.
 */
public class FleetEquipAdapter extends BaseItemTouchHelperAdapter<FleetEquipViewHolder, Fleet.Ship.Equip> {

    private FleetAdapter mFleetAdapter;
    private FleetViewHolder mFleetViewHolder;
    private Ship.EquipEntity mEquipEntity;
    private int mPosition;

    public void setFleetAdapter(FleetAdapter fleetAdapter) {
        mFleetAdapter = fleetAdapter;
    }

    public void setFleetViewHolder(FleetViewHolder fleetViewHolder) {
        mFleetViewHolder = fleetViewHolder;
    }

    public void setEquipEntity(Ship.EquipEntity equipEntity) {
        mEquipEntity = equipEntity;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    @Override
    public FleetEquipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return FleetEquipViewHolder.create(parent, R.layout.item_fleet_ship_equip);
            case 1:
                return FleetEquipViewHolder.create(parent, R.layout.item_fleet_ship_equip_empty);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final FleetEquipViewHolder holder, int position) {
        if (mEquipEntity == null) {
            return;
        }

        resetViewHolderSlot(holder, position);
        resetEquipRelatedText(holder, position);

        if (getItemViewType(position) == 1) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BusProvider.instance().post(new ItemSelectAction.StartEquip(mPosition, holder.getAdapterPosition()));
                }
            });
        } else {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    showEditEquipDialog(context, holder);
                }
            });
        }
    }

    private void showEditEquipDialog(final Context context, final FleetEquipViewHolder holder) {
        ListBottomSheetDialog dialog = new ListBottomSheetDialog(context);
        dialog.setItems(
                new CharSequence[]{
                        context.getString(R.string.fleet_change_equip_attr),
                        context.getString(R.string.fleet_change_equip),
                        context.getString(R.string.fleet_delete_equip)},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                showEditAttrDialog(context, holder);
                                break;
                            case 1:
                                BusProvider.instance().post(new ItemSelectAction.StartEquip(mPosition, holder.getAdapterPosition()));
                                break;
                            case 2:
                                removeEquip(holder.getAdapterPosition());
                                break;
                        }
                        dialogInterface.dismiss();
                    }
                });
        dialog.show();
    }

    @SuppressLint("DefaultLocale")
    private void showEditAttrDialog(Context context, final FleetEquipViewHolder holder) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.content_fleet_equip_attr, null);

        List<String> list;
        list = new ArrayList<>();
        list.add("+0");
        for (int i = 1; i <= 10; i++) {
            list.add(String.format("+%d", i));
        }
        RecyclerView recyclerView;
        recyclerView = (RecyclerView) view.findViewById(R.id.equip_improvement_container);
        SimpleSelectableAdapter<String> improvementAdapter = new SimpleSelectableAdapter<>(R.layout.item_dialog_equip_improvement, true);
        improvementAdapter.setSelection(getItem(holder.getAdapterPosition()).getStar());
        improvementAdapter.setItemList(list);
        recyclerView.setAdapter(improvementAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 6, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildAdapterPosition(view) >= 6) {
                    outRect.top = Utils.dpToPx(4);
                }
            }
        });

        list = new ArrayList<>();
        for (String s : Fleet.equipRank) {
            list.add(s);
        }
        recyclerView = (RecyclerView) view.findViewById(R.id.equip_rank_container);
        SimpleSelectableAdapter<String> rankAdapter = new SimpleSelectableAdapter<>(R.layout.item_dialog_equip_improvement, true);
        rankAdapter.setSelection(getItem(holder.getAdapterPosition()).getRank());
        rankAdapter.setItemList(list);
        recyclerView.setAdapter(rankAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 6, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildAdapterPosition(view) >= 6) {
                    outRect.top = Utils.dpToPx(4);
                }
            }
        });

        dialog.setContentView(view);
        dialog.show();

        improvementAdapter.setListener(new SimpleAdapter.Listener() {
            @Override
            public void OnClick(int position) {
                getItem(holder.getAdapterPosition()).setStar(position);
                resetParent();
                resetEquipRelatedText(holder, holder.getAdapterPosition());
            }
        });

        rankAdapter.setListener(new SimpleAdapter.Listener() {
            @Override
            public void OnClick(int position) {
                getItem(holder.getAdapterPosition()).setRank(position);
                resetParent();
                resetEquipRelatedText(holder, holder.getAdapterPosition());
            }
        });
    }

    private void removeEquip(int position) {
        if (getItem(position) != null) {
            getItem(position).setId(0);
            getItem(position).setRank(0);
            getItem(position).setStar(0);
            notifyItemChanged(position);
        }
    }

    @SuppressLint("SetTextI18n")
    private void resetViewHolderSlot(FleetEquipViewHolder holder, int position) {
        if (mEquipEntity == null) {
            return;
        }

        holder.mSummary.setText(Integer.toString(mEquipEntity.getSpace()[position]));
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) == null) {
            return 1;
        }
        return getItem(position).getId() == 0 ? 1 : 0;
    }

    @Override
    public void onItemMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target, int fromPosition, int toPosition) {
        super.onItemMove(recyclerView, viewHolder, target, fromPosition, toPosition);

        resetViewHolderSlot((FleetEquipViewHolder) viewHolder, toPosition);
        resetViewHolderSlot((FleetEquipViewHolder) target, fromPosition);

        resetParent();
    }

    private void resetParent() {
        if (mFleetAdapter != null && mFleetViewHolder != null) {
            mFleetAdapter.resetEquipRelatedText(mFleetViewHolder, mPosition);
        }
    }

    private void resetEquipRelatedText(FleetEquipViewHolder holder, int position) {
        Context context = holder.itemView.getContext();

        if (getItemViewType(position) == 0) {
            Fleet.Ship.Equip e = getItem(position);

            if (e != null) {
                rikka.akashitoolkit.model.Equip equip = e.getEquip();

                if (equip != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(equip.getName().get(context));
                    if (e.getStar() > 0) {
                        sb.append(" ★+").append(e.getStar());
                    }
                    if (e.getRank() > 0) {
                        sb.append(" ").append(Fleet.equipRank[e.getRank()]);
                    }
                    holder.mTitle.setText(sb.toString());
                    EquipTypeList.setIntoImageView(holder.mIcon, equip.getIcon());

                    holder.mSummary.setEnabled(equip.isAircraft());
                }
            }
        }
    }
}
