package com.fuyang.shensong;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.fuyang.shensong.adapter.SpinnerArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class RealTimeFilterFragment extends BaseFragment {
    /**
     * 包含完整地址列表的Json对象
     */
    private static String operateStateName = "", locateWayName = "", cityName = "", countryName = "", townName = "";
    private JSONObject addrJsonObj;
    private Spinner operateStateSpinner, locateWayChooseSpinner, citySpinner, countrySpinner, townSpinner;
    private String provinceName = "陕西省-1";
    private int regionId = 0, machine_status = 0, bycurpos = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_realtime_filter, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String addrStr = getActivity().getString(R.string.address);
        try {
            addrJsonObj = new JSONObject(addrStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        showActionBack(true);
        operateStateSpinner = (Spinner) view.findViewById(R.id.oper_state_spinner);
        configSpinner(operateStateSpinner, Arrays.asList("==所有状态==", "作业中", "作业异常", "行进中", "停止", "故障"), operateStateName);
        locateWayChooseSpinner = (Spinner) view.findViewById(R.id.locate_way_spinner);
        configSpinner(locateWayChooseSpinner, Arrays.asList("按农机所属区域", "按农机当前区域"), locateWayName);
        citySpinner = (Spinner) view.findViewById(R.id.city_spinner);
        countrySpinner = (Spinner) view.findViewById(R.id.country_spinner);
        townSpinner = (Spinner) view.findViewById(R.id.town_spinner);
        configCitySpinner(getCityList(provinceName));
        operateStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                machine_status = operateStateSpinner.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        locateWayChooseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bycurpos = locateWayChooseSpinner.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                regionId = Integer.valueOf(((String) citySpinner.getSelectedItem()).split("-")[1]);
                configCountrySpinner(getCountryList(provinceName, (String) citySpinner.getSelectedItem()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                regionId = Integer.valueOf(((String) countrySpinner.getSelectedItem()).split("-")[1]);
                if (regionId == 0) {
                    regionId = Integer.valueOf(((String) citySpinner.getSelectedItem()).split("-")[1]);
                }
                configTownSpinner(getTownList(provinceName, (String) citySpinner.getSelectedItem(), (String) countrySpinner.getSelectedItem()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        townSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                regionId = Integer.valueOf(((String) townSpinner.getSelectedItem()).split("-")[1]);
                if (regionId == 0) {
                    regionId = Integer.valueOf(((String) countrySpinner.getSelectedItem()).split("-")[1]);
                    if (regionId == 0) {
                        regionId = Integer.valueOf(((String) citySpinner.getSelectedItem()).split("-")[1]);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        view.findViewById(R.id.btn_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                ((RealTimeFragment) (fragmentManager.
                        findFragmentByTag("RealTimeFragment"))).
                        getRealTimeStatus(machine_status, regionId, bycurpos);
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onDestroyView() {
        operateStateName = (String) operateStateSpinner.getSelectedItem();
        locateWayName = (String) locateWayChooseSpinner.getSelectedItem();
        cityName = (String) citySpinner.getSelectedItem();
        countryName = (String) countrySpinner.getSelectedItem();
        townName = (String) townSpinner.getSelectedItem();
        super.onDestroyView();
    }

    private ArrayList<String> getCityList(String provinceName) {
        ArrayList<String> cityList = new ArrayList<>();
        cityList.add("==所有市==-0");
        try {
            JSONObject provAddrJsonObj = addrJsonObj.getJSONObject(provinceName);
            Iterator<String> provAddrIter = provAddrJsonObj.keys();
            while (provAddrIter.hasNext()) {
                cityList.add(provAddrIter.next());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cityList;
    }

    private ArrayList<String> getCountryList(String provinceName, String cityName) {
        ArrayList<String> list = new ArrayList<>();
        list.add("==所有县==-0");
        try {
            JSONObject provAddrJsonObj = addrJsonObj.getJSONObject(provinceName);
            JSONObject cityAddrJsonObj = provAddrJsonObj.getJSONObject(cityName);
            Iterator<String> iterator = cityAddrJsonObj.keys();
            while (iterator.hasNext()) {
                list.add(iterator.next());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    private ArrayList<String> getTownList(String provinceName, String cityName, String countryName) {
        ArrayList<String> list = new ArrayList<>();
        list.add("==所有镇==-0");
        try {
            JSONObject provAddrJsonObj = addrJsonObj.getJSONObject(provinceName);
            JSONObject cityAddrJsonObj = provAddrJsonObj.getJSONObject(cityName);
            JSONArray countryAddrJsonArrObj = cityAddrJsonObj.getJSONArray(countryName);
            for (int i = 0; i < countryAddrJsonArrObj.length(); i++) {
                list.add((String) countryAddrJsonArrObj.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    private void configCitySpinner(List<String> dataList) {
        configSpinner(citySpinner, dataList, cityName);
        cityName = "";
    }

    private void configCountrySpinner(List<String> dataList) {
        configSpinner(countrySpinner, dataList, countryName);
        countryName = "";
    }

    private void configTownSpinner(List<String> dataList) {
        configSpinner(townSpinner, dataList, townName);
        townName = "";
    }

    private void configSpinner(Spinner spinner, List<String> dataList, String defaultName) {
        //将可选内容与ArrayAdapter连接起来
        ArrayAdapter spinnerAdapter = new SpinnerArrayAdapter(this.getActivity(), R.layout.custom_spiner_text_item, dataList);
        //设置下拉列表的风格
        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        //将adapter 添加到spinner中
        spinner.setAdapter(spinnerAdapter);
        int index = dataList.indexOf(defaultName);
        if (index != -1) {
            spinner.setSelection(index);
        }
    }
}
