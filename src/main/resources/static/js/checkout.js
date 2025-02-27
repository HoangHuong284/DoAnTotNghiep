// const exceptionCode = 417;
var token = localStorage.getItem("token");
async function checkroleUser() {
    var token = localStorage.getItem("token");
    var url = 'http://localhost:8080/api/user/user/check-role-user';
    const response = await fetch(url, {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    if (response.status > 300) {
        window.location.replace('login')
    }
}
var total = 30000;

async function loadCartCheckOut() {
    var url = 'http://localhost:8080/api/cart/user/my-cart' ;
    const response = await fetch(url, {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    var list = await response.json();
    if (list.length == 0) {
        alert("Bạn chưa có sản phẩm nào trong giỏ hàng!");
        window.location.replace("cart");
    }
    document.getElementById("slcartcheckout").innerHTML = list.length
    var main = ''
    for (i = 0; i < list.length; i++) {
        total += Number(list[i].quantity * list[i].product.price);
        main += `<div class="row">
                    <div class="col-lg-2 col-md-3 col-sm-3 col-3 colimgcheck">
                        <img src="${list[i].product.imageBanner}" class="procheckout">
                        <span class="slpro">${list[i].quantity}</span>
                    </div>
                    <div class="col-lg-7 col-md-6 col-sm-6 col-6">
                        <span class="namecheck">${list[i].product.name}</span>
                    </div>
                    <div class="col-lg-3 col-md-3 col-sm-3 col-3 pricecheck">
                        <span>${formatmoneyCheck(list[i].quantity * list[i].product.price)}</span>
                    </div>
                </div>`
    }
    document.getElementById("listproductcheck").innerHTML = main;
    document.getElementById("totalAmount").innerHTML = formatmoneyCheck(total);
    document.getElementById("totalfi").innerHTML = formatmoneyCheck(total);
}


function formatmoneyCheck(money) {
    const VND = new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND',
    });
    return VND.format(money);
}


function checkout() {
    var con = confirm("Xác nhận đặt hàng!");
    if (con == false) {
        return;
    }
    var paytype = $('input[name=paytype]:checked').val()
    if (paytype == "momo") {
        requestPayMentMomo()
    }
    if (paytype == "vnpay") {
        requestPayMentVnpay()
    }
    if (paytype == "cod") {
        paymentCod();
    }
}

async function requestPayMentMomo() {
    var returnurl = 'http://localhost:8080/payment';
    var urlinit = 'http://localhost:8080/api/urlpayment';
    var orderDto = {
        "payType": "PAYMENT_MOMO",
        "address": document.getElementById("stressName").value,
        "fullName": document.getElementById("fullname").value,
        "phone": document.getElementById("phone").value,
        "wardId": document.getElementById("xa").value,
        "note": document.getElementById("ghichudonhang").value,
        "voucherCode": voucherCode,
    }
    window.localStorage.setItem('order', JSON.stringify(orderDto));
    window.localStorage.setItem('paytype',"PAYMENT_MOMO");
    console.log(orderDto)

    var paymentDto = {
        "content": "thanh toán đơn hàng book store",
        "returnUrl": returnurl,
        "notifyUrl": returnurl,
        "codeVoucher": voucherCode,
    }
    const res = await fetch(urlinit, {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(paymentDto)
    });
    var result = await res.json();
    if (res.status < 300) {
        window.open(result.url, '_blank');
    }
    if (res.status == exceptionCode) {
        toastr.warning(result.defaultMessage);
    }

}


async function requestPayMentVnpay() {
    var returnurl = 'http://localhost:8080/payment'; // url return khi ng dung thanh toan thanh cong
    var urlinit = 'http://localhost:8080/api/vnpay/urlpayment';
    var orderDto = {
        "payType": "PAYMENT_VNPAY",
        "address": document.getElementById("stressName").value,
        "fullName": document.getElementById("fullname").value,
        "phone": document.getElementById("phone").value,
        "wardId": document.getElementById("xa").value,
        "note": document.getElementById("ghichudonhang").value,
        "voucherCode": voucherCode,
    }
    window.localStorage.setItem('order', JSON.stringify(orderDto));
    window.localStorage.setItem('paytype',"PAYMENT_VNPAY");
    console.log(orderDto)

    var paymentDto = {
        "content": "thanh toán đơn hàng book store",
        "returnUrl": returnurl,
        "notifyUrl": returnurl,
        "codeVoucher": voucherCode,
    }
    const res = await fetch(urlinit, {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(paymentDto)
    });
    var result = await res.json();
    if (res.status < 300) {
        window.open(result.url, '_blank');
    }
    if (res.status == exceptionCode) {
        toastr.warning(result.defaultMessage);
    }

}

async function paymentOnline() {
    var uls = new URL(document.URL)
    var orderId = uls.searchParams.get("orderId");
    var requestId = uls.searchParams.get("requestId");
    var vnpOrderInfo = uls.searchParams.get("vnp_OrderInfo");
    var paytype = window.localStorage.getItem("paytype")

    var orderDto = JSON.parse(localStorage.getItem("order"));
    if(paytype == "PAYMENT_MOMO"){
        orderDto.requestIdMomo = requestId
        orderDto.orderIdMomo = orderId
    }
    else{
        orderDto.vnpOrderInfo = vnpOrderInfo;
        const currentUrl = window.location.href;
        const parsedUrl = new URL(currentUrl);
        const queryStringWithoutQuestionMark = parsedUrl.search.substring(1); // lay tat ca cac du lieu tu url
        orderDto.urlVnpay = queryStringWithoutQuestionMark;
    }
    var url = 'http://localhost:8080/api/invoice/user/create';
    var token = localStorage.getItem("token");
    const res = await fetch(url, {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(orderDto)
    });
    var result = await res.json();
    if (res.status < 300) {
        document.getElementById("thanhcong").style.display = 'block'
    }
    if (res.status == exceptionCode) {
        document.getElementById("thatbai").style.display = 'block'
        document.getElementById("thanhcong").style.display = 'none'
        document.getElementById("errormess").innerHTML = result.defaultMessage
    }

}




async function paymentCod() {
    var orderDto = {
        "payType": "PAYMENT_DELIVERY",
        "address": document.getElementById("stressName").value,
        "fullName": document.getElementById("fullname").value,
        "phone": document.getElementById("phone").value,
        "wardId": document.getElementById("xa").value,
        "note": document.getElementById("ghichudonhang").value,
        "voucherCode": voucherCode,
    }
    var url = 'http://localhost:8080/api/invoice/user/create';
    var token = localStorage.getItem("token");
    const res = await fetch(url, {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(orderDto)
    });
    if (res.status < 300) {
        swal({
                title: "Thông báo",
                text: "Đặt hàng thành công!",
                type: "success"
            },
            function() {
                window.location.replace("account#invoice")
            });
    }
}


async function getUserInfor() {
    var token = localStorage.getItem("token");
    var url = 'http://localhost:8080/api/user/user/user-logged';
    const response = await fetch(url, {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    var user = await response.json();
    console.log(user);
    document.getElementById("phone").value = user.phone
    document.getElementById("fullname").value = user.fullName
}


var voucherId = null;
var voucherCode = null;
var discountVou = 0;
async function loadVoucher() {
    var code = document.getElementById("codevoucher").value
    var url = 'http://localhost:8080/api/voucher/public/findByCode?code=' + code + '&amount=' + (total);
    alert(total)
    const response = await fetch(url, {});
    var result = await response.json();
    if (response.status == exceptionCode) {
        var mess = result.defaultMessage
        document.getElementById("messerr").innerHTML = mess;
        document.getElementById("blockmessErr").style.display = 'block';
        document.getElementById("blockmess").style.display = 'none';
        voucherCode = null;
        voucherId = null;
        discountVou = 0;
        document.getElementById("moneyDiscount").innerHTML = formatmoneyCheck(0);
        document.getElementById("totalfi").innerHTML = formatmoneyCheck(total);
    }
    if (response.status < 300) {
        voucherId = result.id;
        voucherCode = result.code;
        discountVou = result.discount;
        document.getElementById("blockmessErr").style.display = 'none';
        document.getElementById("blockmess").style.display = 'block';
        document.getElementById("moneyDiscount").innerHTML = formatmoneyCheck(result.discount);
        document.getElementById("totalfi").innerHTML = formatmoneyCheck(total - result.discount);
    }

}
