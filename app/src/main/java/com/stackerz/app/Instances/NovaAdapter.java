package com.stackerz.app.Instances;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stackerz.app.Endpoints.EndpointsParser;
import com.stackerz.app.R;
import com.stackerz.app.System.ObscuredSharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ed on 19/11/14.
 */
public class NovaAdapter extends RecyclerView.Adapter<NovaListRowHolder> {

    ArrayList<HashMap<String, String>> novaList = new ArrayList<HashMap<String, String>>();
    public static final String STATUS = "status";
    public static final String NAME = "name";
    public static final String FLAVOR = "flavor";
    public static final String ID = "id";
    public static final String NETID = "netid";
    public static final String ADDR = "addr";
    public static final String HOST = "host";
    private Context mContext;

    public NovaAdapter(Context context, ArrayList<HashMap<String, String>> novaList) {
        this.novaList = novaList;
        this.mContext = context;
    }

    @Override
    public NovaListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.instances_list, null);
        NovaListRowHolder mh = new NovaListRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(NovaListRowHolder novaListRowHolder, int i) {

        HashMap<String, String> e = novaList.get(i);
        novaListRowHolder.name.setText(e.get(NAME));
        novaListRowHolder.status.setText(e.get(STATUS));
        novaListRowHolder.host.setText("host: "+e.get(HOST));
        novaListRowHolder.setId(e.get(ID));


    }


    @Override
    public int getItemCount() {
        return (null != novaList ? novaList.size() : 0);
    }

}

class NovaListRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    protected TextView name;
    protected TextView status;
    protected TextView host;
    protected String id;
    private int mOriginalHeight = 0;
    private boolean mIsViewExpanded = false;
    RelativeLayout main;
    RelativeLayout expanded;
    ImageButton start,pause,stop,snapshot,info;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public NovaListRowHolder(View view) {
        super(view);
        view.setOnClickListener(this);
        this.name = (TextView) view.findViewById(R.id.nameInstance);
        this.status = (TextView) view.findViewById(R.id.statusInstance);
        this.host = (TextView) view.findViewById(R.id.hostInstance);
        main = (RelativeLayout)view.findViewById(R.id.layoutInstances);
        expanded = (RelativeLayout)view.findViewById(R.id.expandedInstances);
        this.start = (ImageButton)view.findViewById(R.id.start_buttonInstances);
        this.pause = (ImageButton)view.findViewById(R.id.pause_buttonInstances);
        this.stop = (ImageButton)view.findViewById(R.id.stop_buttonInstances);
        this.snapshot = (ImageButton)view.findViewById(R.id.snapshot_buttonInstances);
        this.info = (ImageButton)view.findViewById(R.id.info_buttonInstances);
        expanded.setVisibility(View.GONE);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Start Instance");
                alert.setMessage("Do you want to start the instance "+name.getText()+" ?")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NovaJSON.shared().startJSON(getId());
                                Toast.makeText(v.getContext(), "Start Request sent to server", Toast.LENGTH_LONG).show();
                                status.setText("start requested");
                            }
                        });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();

            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String currentStatus;
                currentStatus = status.getText().toString();
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Pause/Unpause Instance");
                if (currentStatus.contains("pause")) {
                    alert.setMessage("Do you want to unpause the instance " + name.getText() + " ? Use the same button to pause it again if necessary.")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    NovaJSON.shared().unpauseJSON(getId());
                                    Toast.makeText(v.getContext(), "Unpause Request sent to server", Toast.LENGTH_LONG).show();
                                    status.setText("unpause requested");
                                }
                            });
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                }else {
                    alert.setMessage("Do you want to pause the instance " + name.getText() + " ? Use the same button to unpause it if necessary.")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    NovaJSON.shared().pauseJSON(getId());
                                    Toast.makeText(v.getContext(), "Pause Request sent to server", Toast.LENGTH_LONG).show();
                                    status.setText("pause requested");
                                }
                            });
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Stop Instance");
                alert.setMessage("Do you want to stop the instance "+name.getText()+" ?")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NovaJSON.shared().stopJSON(getId());
                                Toast.makeText(v.getContext(), "Stop Request sent to server", Toast.LENGTH_LONG).show();
                                status.setText("stop requested");
                            }
                        });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            }
        });

        snapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Take Snapshot");
                alert.setMessage("Do you want to take a snapshot of the instance "+name.getText()+" ?")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NovaJSON.shared().backupJSON(getId(),name.getText().toString());
                                Toast.makeText(v.getContext(), "Snapshot Request sent to server", Toast.LENGTH_LONG).show();
                            }
                        });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();

            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<HashMap<String, String>> netList = new ArrayList<HashMap<String, String>>();
                ArrayList<HashMap<String, String>> secList = new ArrayList<HashMap<String, String>>();
                String instanceDetail = NovaJSON.shared().receiveDetail(getId());
                Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.instances_info);
                TextView image = (TextView) dialog.findViewById(R.id.imageInstance);
                TextView flavor = (TextView) dialog.findViewById(R.id.flavorInstance);
                TextView net = (TextView) dialog.findViewById(R.id.netInstance);
                TextView sec = (TextView) dialog.findViewById(R.id.secInstance);
                dialog.setTitle(name.getText() + " Details");
                if (instanceDetail != null) {
                    image.setText(" \u2022 image : " + NovaParser.shared().parseImages(instanceDetail));
                    flavor.setText(" \u2022 flavor : " + NovaParser.shared().parseFlavor(instanceDetail));
                    netList = NovaParser.shared().parseNet(instanceDetail);
                    secList = NovaParser.shared().parseSec(instanceDetail);
                    String netTemp = netList.toString();
                    netTemp = netTemp.replace("["," ");
                    netTemp = netTemp.replace("{","");
                    netTemp = netTemp.replace("]","");
                    netTemp = netTemp.replace("}","");
                    netTemp = netTemp.replace(",","\n");
                    netTemp = netTemp.replace("="," : ");
                    netTemp = netTemp.replace("address","• address");
                    netTemp = netTemp.replace("network","     network");
                    netTemp = netTemp.replace("type","     type");
                    net.setText(netTemp);
                    String secTemp = secList.toString();
                    secTemp = secTemp.replace("["," ");
                    secTemp = secTemp.replace("{","");
                    secTemp = secTemp.replace("]","");
                    secTemp = secTemp.replace("}","");
                    secTemp = secTemp.replace(",","\n");
                    secTemp = secTemp.replace("="," : ");
                    secTemp = secTemp.replace("security group","• security group");
                    sec.setText(secTemp);
                }
                dialog.show();
            }
        });

    }

    public void onClick(final View view){
        if (mOriginalHeight == 0) {
            mOriginalHeight = view.getHeight();
        }
        ValueAnimator valueAnimator;
        if (!mIsViewExpanded) {
            mIsViewExpanded = true;
            valueAnimator = ValueAnimator.ofInt(mOriginalHeight, mOriginalHeight + (int) (mOriginalHeight * 1.0));
            ObjectAnimator anim = ObjectAnimator.ofFloat(expanded, "alpha", 0f, 1f);
            anim.setDuration(1000);
            anim.start();
            expanded.setVisibility(View.VISIBLE);
        } else {
            mIsViewExpanded = false;
            valueAnimator = ValueAnimator.ofInt(mOriginalHeight + (int) (mOriginalHeight * 1.0), mOriginalHeight);
            ObjectAnimator anim = ObjectAnimator.ofFloat(expanded, "alpha", 1f, 0f);
            anim.setDuration(1000);
            anim.start();
            expanded.setVisibility(View.GONE);

        }
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                view.getLayoutParams().height = value.intValue();
                view.requestLayout();
            }
        });
        valueAnimator.start();

    }



}
