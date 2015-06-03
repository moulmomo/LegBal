package com.example.mohamed.legapp2;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;


import java.util.ArrayList;


public class MainActivity extends Activity {

    private BluetoothAdapter btAdapter;
    private BluetoothManager btManager;
    private boolean mScanning;
    private Handler mHandler;
    private LeDeviceListAdapter mLeDeviceListAdapter;

    //public String texte="null";
    private ArrayList devices;
    private ArrayList<Beacon> beacons;
    private ArrayAdapter<String> listAdapter ;

    final Activity parent = this;


    private static final int REQUEST_ENABLE_BT=1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        devices = new ArrayList();
        beacons = new ArrayList<Beacon>();
        setContentView(R.layout.activity_main);

        btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
        }
        ListView list = (ListView) findViewById(R.id.list);
        listAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        listAdapter.add(" ");
        list.setAdapter(listAdapter);

        listAdapter.clear();
        devices.clear();
        beacons.clear();
        scanLeDevice(true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {

            //noinspection SimplifiableIfStatement
            case R.id.action_settings:
                return true;

            case R.id.menu_scan:
                listAdapter.clear();
                devices.clear();
                beacons.clear();
                //mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;

            case R.id.menu_stop:
                scanLeDevice(false);
                break;

            case R.id.menu_localisation:
                Intent intent = new Intent(parent,com.example.mohamed.legapp2.LocalisationActivity.class);
                intent.putParcelableArrayListExtra("key", beacons);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    btAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            btAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            btAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

// Device scan callback.
private BluetoothAdapter.LeScanCallback mLeScanCallback =
        new BluetoothAdapter.LeScanCallback() {

            @Override
            public void onLeScan(final BluetoothDevice device,final int rssi, final byte[] scanRecord) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //mLeDeviceListAdapter.addDevice(device);
                        //mLeDeviceListAdapter.notifyDataSetChanged();
                        Beacon beacon = new Beacon();
                        beacon.major = getMajor(scanRecord);
                        beacon.minor = getMinor(scanRecord);
                        beacon.rssi = rssi;
                        String texte= "Name : "+device.getName()+" Adresse : "+device.getAddress()+" Major : "+beacon.major+" Minor : "+beacon.minor+" RSSI : "+rssi;
                        if(!devices.contains(device)){
                            devices.add(device);
                            beacons.add(beacon);
                            listAdapter.add(texte);
                        }
                        else{
                            int i = devices.indexOf(device);
                            int l = devices.size();
                            ArrayList copy = new ArrayList();
                            for(int j=0;j<l;j++) {
                                String item = listAdapter.getItem(j);
                                copy.add(item);
                            }
                            copy.set(i,texte);
                            listAdapter.clear();
                            listAdapter.addAll(copy);
                            //listAdapter.notifyDataSetChanged();
                        }
                        //listAdapter.notifyDataSetChanged();


                    }
                });
            }
        };

    // Methods to get the scanned device's major and minor
    public String getMajor(byte[] mScanRecord) {
        String major = String.valueOf( (mScanRecord[25] & 0xff) * 0x100 + (mScanRecord[26] & 0xff));
        return major;
    }

    public String getMinor(byte[] mScanRecord) {
        String minor = String.valueOf( (mScanRecord[27] & 0xff) * 0x100 + (mScanRecord[28] & 0xff));
        return minor;
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = MainActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
          /*  ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());*/

            return view;
        }
    }

}
