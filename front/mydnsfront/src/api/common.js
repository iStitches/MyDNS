export function trim(str){ //删除左右两端的空格
　　     return str.replace(/(^\s*)|(\s*$)/g, "");
}
export function ltrim(str){ //删除左边的空格
　　     return str.replace(/(^\s*)/g,"");
}
export function rtrim(str){ //删除右边的空格
　　     return str.replace(/(\s*$)/g,"");
}
export function changeDateFormat(date) {
     var hour = date.getHours()<10?"0"+date.getHours():date.getHours()+""
     var minutes = date.getMinutes()<10?"0"+date.getMinutes():date.getMinutes()+""
     var seconds = date.getSeconds()<10?"0"+date.getSeconds():date.getSeconds()+""
     return hour+":"+minutes+":"+seconds
}