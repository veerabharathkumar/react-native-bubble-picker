package com.reactlibrary;


import android.graphics.Color;
import android.widget.FrameLayout;
import android.content.Context;
import android.view.ViewManager;

import java.util.ArrayList;
import java.util.List;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.igalata.bubblepicker.BubblePickerListener;
import com.igalata.bubblepicker.rendering.BubblePicker;
import com.igalata.bubblepicker.adapter.BubblePickerAdapter;
import com.igalata.bubblepicker.model.PickerItem;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.bridge.ReactApplicationContext;


public class BubblePickerView extends SimpleViewManager<BubblePicker> {
    float FONT_RATIO = 1.5f;
    float RADIUS_RATIO = 0.45f;

    class Item {
        String id;
        String text;
        String color;
        String textColor;
        boolean isSelected;

        public Item(){}
        public Item(ReadableMap map){
            id = map.getString("id");
            text = map.getString("text");
            color = map.getString("color");
            textColor = map.getString("textColor");
            isSelected = map.getBoolean("isSelected");
        }
        public WritableMap toJsMap(){

            WritableMap map = Arguments.createMap();

            map.putString("id", id);
            map.putString("text", text);
            map.putString("color", color);
            map.putString("textColor", textColor);
            map.putBoolean("isSelected", isSelected);

            return map;
        }
    }

//    Callback onPress;
    BubblePicker bubblePicker;
    BubblePickerAdapter bubblePickerAdapter;
    List<Item> items = new ArrayList<Item>();
    float fontSize = 25 * FONT_RATIO;
    float radius = 30 * RADIUS_RATIO;

    public static final String REACT_CLASS = "BubblePickerView";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactProp(name = "items")
    public void setSrc(BubblePicker view, ReadableArray jsItems) {
        items.clear();
        for (int i = 0; i < jsItems.size(); i++) {
            ReadableMap jsItem = jsItems.getMap(i);
            items.add(new Item(jsItem));
        }

        bubblePicker.setAdapter(bubblePickerAdapter);
    }

    @ReactProp(name = "radius", defaultInt = 30)
    public void setRadius(BubblePicker view, int radius){
        this.radius = radius;
        bubblePicker.setBubbleSize((int)(radius * RADIUS_RATIO)); // equalizing sizes from ios to android
        bubblePicker.setAdapter(bubblePickerAdapter);
    }

    @ReactProp(name = "fontSize", defaultFloat = 10f)
    public void setFontSize(BubblePicker view, float fontSize){
        this.fontSize = fontSize * FONT_RATIO;  // equalizing sizes from ios to android
        bubblePicker.setAdapter(bubblePickerAdapter);
    }

    @Override
    public BubblePicker createViewInstance(final ThemedReactContext context) {
        bubblePicker = new BubblePicker(context);
        bubblePicker.setCenterImmediately(true);
        bubblePicker.setZOrderOnTop(false);
        final float fontSize = this.fontSize;

        bubblePickerAdapter = new BubblePickerAdapter() {
            @Override
            public int getTotalCount() {
                return items.size();
            }

            @Override
            public PickerItem getItem(int position) {
                //ListItem preferencesItem = predictorDataModel.PreferencesForSchool.get(position);
                //if(preferencesItem == null) return null;
                PickerItem item = new PickerItem();

                Item srcItem = items.get(position);

                item.setSelected(srcItem.isSelected);
                item.setTitle(srcItem.text);
                item.setOverlayAlpha(1);
                item.setColor(Color.parseColor(srcItem.color));
                item.setTextColor(Color.parseColor(srcItem.textColor));
                item.setCustomData(srcItem);
                item.setTextSize(fontSize);
                return item;
            }
        };
        bubblePicker.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(final PickerItem pickerItem) {

                Item item = (Item) pickerItem.getCustomData();
                item.isSelected = true;
                context
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("onSelected",item.toJsMap());
            }

            @Override
            public void onBubbleDeselected(PickerItem pickerItem) {
                Item item = (Item) pickerItem.getCustomData();
                item.isSelected = false;
                context
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("onDeselected",item.toJsMap());
            }
        });

        bubblePicker.setAdapter(bubblePickerAdapter);

        return bubblePicker;
    }
}