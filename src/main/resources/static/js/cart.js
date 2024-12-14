async function addCart(type) {
    if(token == null){
        toastr.error("Bạn chưa đăng nhập");
        return
    }
    var quantity = document.getElementById("inputslcart").value
    var uls = new URL(document.URL)
    var id = uls.searchParams.get("id");
    var url = 'http://localhost:8080/api/cart/user/create?productId='+id+"&quantity="+quantity;
    const response = await fetch(url, {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
        })
    });
    if (response.status < 300) {
        toastr.success("Thêm giỏ hàng thành công!");
        loadCartMenu();
        if(type == 0){
            window.location.href = 'cart'
        }
    }
    else {
        toastr.erorr("Thêm giỏ hàng thất bại!");
    }
}

async function loadAllCart() {
    if(token == null){
        window.location.href = 'login'
    }
    var url = 'http://localhost:8080/api/cart/user/my-cart' ;
    const response = await fetch(url, {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    var list = await response.json();
    console.log(list)
    var main = ''
    var total = 0;
    for (i = 0; i < list.length; i++) {
        main += `<tr>
                    <td>
                        <a href="detail?id=${list[i].product.id}&name=${list[i].product.name}"><img class="imgprocart" src="${list[i].product.imageBanner}"></a>
                        <div class="divnamecart">
                            <a href="detail?id=${list[i].product.id}&name=${list[i].product.name}" class="nameprocart">${list[i].product.name}</a>
                        </div>
                    </td>
                    <td><p class="boldcart">${formatmoney(list[i].product.price)}</p></td>
                    <td>
                        <div class="clusinp"><button onclick="upDownQuantity(${list[i].id},'DOWN')" class="cartbtn"> - </button>
                        <input value="${list[i].quantity}" class="inputslcart">
                        <button onclick="upDownQuantity(${list[i].id},'UP')" class="cartbtn"> + </button></div>
                    </td>
                    <td>
                        <div class="tdpricecart">
                            <p class="boldcart">${formatmoney(list[i].quantity * list[i].product.price)}</p>
                            <p onclick="remove(${list[i].id})" class="delcart"><i class="fa fa-trash-o facartde"></i></p>
                        </div>
                    </td>
                </tr>`
        total += Number(list[i].quantity * list[i].product.price)
    }
    document.getElementById("listcartDes").innerHTML = main
    loadAllCartMobile();
    document.getElementById("slcart").innerHTML = list.length
    document.getElementById("tonggiatien").innerHTML = formatmoney(total)
}

async function loadAllCartMobile() {
    if(token == null){
        window.location.href = 'login'
    }
    var url = 'http://localhost:8080/api/cart/user/my-cart' ;
    const response = await fetch(url, {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    var list = await response.json();
    var main = ''
    for (i = 0; i < list.length; i++) {
        main += `<tr>
        <td>
            <a href="detail?id=${list[i].product.id}&name=${list[i].product.name}"><img class="imgprocmobile" src="${list[i].product.imageBanner}"></a>
        </td>
        <td class="tdinforcart">
            <a href="detail?id=${list[i].product.id}&name=${list[i].product.name}" class="nameprocmobile">${list[i].product.name}</a>
            <div class="clusinpmobile">
                <button onclick="upDownQuantity(${list[i].id},'DOWN')" class="cartbtn"> - </button>
                <input value="${list[i].quantity}" class="inputslcart">
                <button onclick="upDownQuantity(${list[i].id},'UP')" class="cartbtn"> + </button>
            </div>
        </td>
        <td class="tdinforcart">
            <i onclick="remove(${list[i].id})" class="fa fa-trash-o facartde cartdels"></i>
            <p class="boldcart pricecmobile">${formatmoney(list[i].quantity * list[i].product.price)}&ThinSpace;</p>
        </td>
    </tr>`
    }
    document.getElementById("tablemobilecart").innerHTML = main
}

async function remove(id) {
    var con = confirm("Bạn muốn xóa sản phẩm này khỏi giỏ hàng?")
    if(con == false){
        return;
    }
    var url = 'http://localhost:8080/api/cart/user/delete?id='+id ;
    const response = await fetch(url, {
        method: 'DELETE',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    if(response.status < 300){
        loadAllCart();
        loadCartMenu();
    }
}

async function upDownQuantity(id, type) {
    var url = 'http://localhost:8080/api/cart/user/down-cart?id='+id ;
    if(type == "UP"){
        url = 'http://localhost:8080/api/cart/user/up-cart?id='+id ;
    }
    const response = await fetch(url, {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    if(response.status < 300){
        loadAllCart();
    }
}


async function productLqCart() {
    var listTrademark = [];
    var url = 'http://localhost:8080/api/cart/user/my-cart' ;
    const response = await fetch(url, {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    var list = await response.json();
    for (i = 0; i < list.length; i++) {
        listTrademark.push(list[i].product.category.id)
    }


    var url = 'http://localhost:8080/api/product/public/search-full?page=0&size=4&sort=id,desc';
    var obj = {
        "categoryIds":listTrademark,
        "minPrice":0,
        "maxPrice":100000000
    }
    console.log(obj);
    const res = await fetch(url, {
        method: 'POST',
        headers: new Headers({
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(obj)
    });
    var result = await res.json();
    var list = result.content
    console.log(list);
    var main = ''
    for (i = 0; i < list.length; i++) {
        var listimg = ''
        for (j = 0; j < list[i].productImages.length; j++) {
            listimg += `<div class="divimgsmpro"><img class="imgsmpro" src="${list[i].productImages[j].linkImage}"></div>`
        }
        main += `<div class="col-lg-3 col-md-3 col-sm-6 col-6">
                <a href="detail?id=${list[i].id}&name=${list[i].name}" class="linkpro">
                    <div class="singlepro">
                        <div class="productsold"><span class="reviewsp">Đã bán: ${list[i].quantitySold}</span></div>
                        <img src="${list[i].imageBanner}" class="imgpro">
                        <span class="proname">${list[i].name}</span>
                        <span class="proprice">${formatmoney(list[i].price)}</span>
                        <div class="listimgpro">${listimg}</div>
                    </div>
                </a>
            </div>`
    }
    document.getElementById("listproductgycart").innerHTML = main;
}