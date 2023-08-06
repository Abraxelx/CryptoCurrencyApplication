package com.abraxel.cryptocurrency.interfaces;


import com.abraxel.cryptocurrency.model.ChartData;
import org.json.JSONObject;

import java.util.List;

public interface IVolley {
    List<ChartData> onResponse(JSONObject response);
}
