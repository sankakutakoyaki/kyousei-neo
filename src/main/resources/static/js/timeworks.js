"use strict"

async function setTimeworks(timeCategory) {
    const time = getTimeNow();
    const position = await getLoacation();
    if (timeCategory == "start") {
        entity.start_time = time;
        entity.start_latitude = position.coords.latitude;
        entity.start_longitude = position.coords.longitude;
    } else if (timeCategory == "end") {
        entity.end_time = time;
        entity.end_latitude = position.coords.latitude;
        entity.end_longitude = position.coords.longitude;
    } else {
        return null;
    }
    const data = JSON.stringify(entity);
    const url = '/timeworks/regist/today';
    const contentType = 'application/json';
    const result = await postFetch(url, data, token, contentType);

    await updateDisplay();
    
    return await result.json();
}