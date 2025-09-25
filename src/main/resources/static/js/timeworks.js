"use strict"

async function setTimeworks(timeCategory) {
    const time = getTimeNow();
    const position = await getLoacation();
    if (timeCategory == "start") {
        entity.start_time = time;
        entity.comp_start_time = time;
        entity.start_latitude = position.coords.latitude;
        entity.start_longitude = position.coords.longitude;
    } else if (timeCategory == "end") {
        entity.end_time = time;
        entity.comp_end_time = time;
        entity.end_latitude = position.coords.latitude;
        entity.end_longitude = position.coords.longitude;
    } else {
        return null;
    }
    const data = JSON.stringify(entity);
    const url = '/timeworks/regist/today';
    const contentType = 'application/json';
    const resultResponse = await postFetch(url, data, token, contentType);
    const result = await resultResponse.json();

    await updateDisplay(result);
    // if (result.data == -1) {
    //     funcHandlingErrors(result.message);
    // } else {
    //     await updateDisplay(result);
    // }    
}

async function updateTimeworks(list, self) {
    const data = JSON.stringify(list);
    const url = '/timeworks/update/list';
    const contentType = 'application/json';
    const result = await postFetch(url, data, token, contentType);

    await execListChange(self);
    
    return await result.json();
}

function zeroTimeCheck(self) {
    if (self.value === "00:00:00") {
        self.classList.add("zero-time");
    } else {
        self.classList.remove("zero-time");
    }
}