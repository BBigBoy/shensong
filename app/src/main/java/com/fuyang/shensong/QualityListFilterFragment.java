package com.fuyang.shensong;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.fuyang.shensong.adapter.SpinnerArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class QualityListFilterFragment extends BaseFragment {
    private static String cityName = "", countryName = "", townName = "";
    private JSONObject addrJsonObj;
    private Spinner citySpinner, countrySpinner, townSpinner;
    private String provinceName = "陕西省-1";
    private int regionId = 0;
    private EditText startYear, startMonth, startDay, endYear, endMonth, endDay;
    QualityListFragment qualityListFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        qualityListFragment = ((QualityListFragment) (fragmentManager.
                findFragmentByTag("QualityListFragment")));
        return inflater.inflate(R.layout.fragment_qualitylist_filter, container, false);
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
        showActionBack(true);
        citySpinner = (Spinner) view.findViewById(R.id.city_spinner);
        countrySpinner = (Spinner) view.findViewById(R.id.country_spinner);
        townSpinner = (Spinner) view.findViewById(R.id.town_spinner);
        configCitySpinner(getCityList(provinceName));
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
        startYear = (EditText) view.findViewById(R.id.start_year);
        startMonth = (EditText) view.findViewById(R.id.start_month);
        startDay = (EditText) view.findViewById(R.id.start_day);
        endYear = (EditText) view.findViewById(R.id.end_year);
        endMonth = (EditText) view.findViewById(R.id.end_month);
        endDay = (EditText) view.findViewById(R.id.end_day);
        Calendar today = Calendar.getInstance();
        endYear.setText(today.get(Calendar.YEAR) + "");
        endMonth.setText(today.get(Calendar.MONTH) + 1 + "");
        endDay.setText(today.get(Calendar.DAY_OF_MONTH) + "");
        view.findViewById(R.id.btn_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startYearStr = startYear.getText().toString();
                String startMonthStr = startMonth.getText().toString();
                String startDayStr = startDay.getText().toString();
                String endYearStr = endYear.getText().toString();
                String endMonthStr = endMonth.getText().toString();
                String endDayStr = endDay.getText().toString();
                if (!validEditTextContent(startYearStr, 2050, 2000)) {
                    displayToast("请输入正确的起始年时间！");
                    return;
                } else if (!validEditTextContent(startMonthStr, 12, 1)) {
                    displayToast("请输入正确的起始月时间！");
                    return;
                } else if (!validEditTextContent(startDayStr, 31, 1)) {
                    displayToast("请输入正确的起始日时间！");
                    return;
                } else if (!validEditTextContent(endYearStr, 2050, 2000)) {
                    displayToast("请输入正确的截止年时间！");
                    return;
                } else if (!validEditTextContent(endMonthStr, 12, 1)) {
                    displayToast("请输入正确的截止月时间！");
                    return;
                } else if (!validEditTextContent(endDayStr, 31, 1)) {
                    displayToast("请输入正确的截止日时间！");
                    return;
                }
                String startTime = startYearStr + "-" + String.format("%02d", Integer.valueOf(startMonthStr)) + "-" + String.format("%02d", Integer.valueOf(startDayStr));
                String endTime = endYearStr + "-" + String.format("%02d", Integer.valueOf(endMonthStr)) + "-" + String.format("%02d", Integer.valueOf(endDayStr));
                qualityListFragment.setEndTime(endTime);
                qualityListFragment.setStartTime(startTime);
                qualityListFragment.setRegionId(regionId);
                qualityListFragment.getQualityList(true);
                getActivity().onBackPressed();
                displayToast("筛选成功");
            }
        });
        String startTime = qualityListFragment.getStartTime();
        String endTime = qualityListFragment.getEndTime();
        //if (regionId != 0) {
        // }
        if (!startTime.equals("")) {
            String[] arr = startTime.split("-");
            startYear.setText(arr[0]);
            startMonth.setText(arr[1]);
            startDay.setText(arr[2]);
        }
        if (!endTime.equals("")) {
            String[] arr = endTime.split("-");
            endYear.setText(arr[0]);
            endMonth.setText(arr[1]);
            endDay.setText(arr[2]);
        }
    }

    private boolean validEditTextContent(String editTextStr, int maxStr, int minStr) {
        if (editTextStr.trim().equals("")) {
            return false;
        } else if (Integer.valueOf(editTextStr) > maxStr || Integer.valueOf(editTextStr) < minStr) {
            return false;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
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
