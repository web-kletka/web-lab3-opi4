const result_field = document.getElementById("myform:result_field");

validate = (params) => {
    let x = params.get("myform:x");
    let y = params.get("myform:y");
    let r = params.get("myform:r");
    let result = "";
    console.log(x, y, r);


    if (x == null) {
        result = "ERROR: x = null";
        console.log("ERROR: x = null");
    }
    else if (y == null) {
        result = "ERROR: y = null";
        console.log("ERROR: y = null");
    }
    else if (y === '') {
        result = "ERROR: y = ''";
        console.log("ERROR: y = ''");
    }
    else if (x === '') {
        result = "ERROR: x = ''";
        console.log("ERROR: x = ''");
    }
    else if (Number.isNaN(Number(y))) {
        result = "ERROR: y not num";
        console.log("ERROR: y not num");
    }
    else if (Number.isNaN(Number(x))) {
        result = "ERROR: x not num";
        console.log("ERROR: x not num");
    }
    else if (r == null) {
        result = "ERROR: r = null";
    }
    if (result === "")
        return true;
    else {
        result_field.textContent = result;
        return false;
    }
}


function validateForm() {
    let formData = new FormData(document.getElementById("myform"))
    console.log(formData)
    let params = new URLSearchParams(formData);
    console.log(params.toString())
    if (!validate(params)) {
        console.log("Отменить отправку формы")
        return false;
    }
    console.log("Если всё хорошо")
    return true;
}

