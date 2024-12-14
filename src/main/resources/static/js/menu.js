var token = localStorage.getItem("token");
const exceptionCode = 417;
var tokenFcm = "";
async function loadMenu() {
    var dn = `<span class="nav-item dropdown pointermenu gvs">
                <i class="fa fa-user" class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false"> Tài khoản</i>
                <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
                    <li><a class="dropdown-item" href="account">Tài khoản</a></li>
                    <li onclick="logout()"><a class="dropdown-item" href="#">Đăng xuất</a></li>
                </ul>
            </span>`
    if (token == null) {
        dn = `<a href="login" class="pointermenu gvs"><i class="fa fa-user"> Đăng ký/ Đăng nhập</i></a>`
    }
    var menu =
        `<div id="topmenu" class="topmenu container-fluid row">
        <div class="col-sm-2"><a href="index" class="linktop"><img style="width: 160px;" src="image/logologin.png"></a></div>
        <div class="col-sm-6">
            <div class="searchmenu">
                <form action="product"><input name="search" onkeyup="searchMenu()" class="imputsearchmenu" placeholder="Tìm kiếm" id="inputsearchmenu" aria-describedby="basic-addon1" autocomplete="off" >
                <ul class="dropdown-menu" aria-labelledby="dropdownMenu1" id="listproductsearch">
                    <i onclick="document.getElementById('listproductsearch').style.display='none'" class="fa fa-remove closesearhc"></i>
                    <div id="listproductmn">
                        <a href="" class="tenspsearch"><div class="singlesearch col-md-12">
                            <div class="row">
                                <div class="col-2"><img class="imgprosearch" src="image/pro.webp"></div>
                                <div class="col-10">
                                    <span class="tenspsearch">Chân váy nữ dáng A</span><br>
                                    <span class="tenspsearch">214.500đ</span>
                                </div>
                            </div>
                        </div></a>
                    </div>
                </ul>
                <button class="btnsearchmenu"><i class="fa fa-search"></i></button></form>
            </div>
        </div>
        <div class="col-sm-4 addrmenu">
            <a href="" class="amens pointermenu"><i class="amens fa fa-info-circle gvs"> Về chúng tôi</i></a>
            <a href="" class="amens pointermenu"><i class="amens fa fa-map-marker gvs"> Xem địa chỉ</i></a>
            <a href="" class="amens pointermenu"><i class="amens fa fa-phone"> 0338256771</i></a>
        </div>
    </div>
    <nav class="navbar navbar-expand-lg">
        <div class="container-fluid">
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <a class="navbar-brand navbar-toggler" href="index"><img style="width: 140px;" src="image/logologin.png"></a>
            <span>
                <i data-bs-toggle="modal" data-bs-target="#modalsearch" class="fa fa-search navbar-toggler"></i>
                <a href="cart" class="pointermenu"><i class="fa fa-shopping-bag navbar-toggler"> <span id="slcartmenusm" class="slcartmenusm">0</span></i></a>
            </span>
            <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0" id="mainmenut">
                <li class="nav-item"><a class="nav-link menulink" href="index">Trang chủ</a></li>
                <li class="nav-item"><a class="nav-link menulink" href="blog">Blog</a></li>
            </ul>
            <div class="d-flex">
                ${dn}
                <a href="cart" class="pointermenu"><i class="fa fa-shopping-bag"><span class="slcartmenu" id="slcartmenu">0</span> Giỏ hàng</i></a>
            </div>
        </div>
    </nav>`
    document.getElementById("menu").innerHTML = menu
    loadCategoryMenu();
    loadCartMenu();
    try { loadFooter(); } catch (error) {}
}


async function loadCategoryMenu() {
    var url = 'http://localhost:8080/api/category/public/all-category';
    const response = await fetch(url, {});
    var list = await response.json();
    var main = ''
    for (i = 0; i < list.length; i++) {
        main += `<li class="nav-item dropdown ddtog">
        <a class="nav-link menulink ddtog" href="product?danhmuc=${list[i].id}" >${list[i].name}</a>
        </li>`
    }
    document.getElementById("mainmenut").innerHTML += main;
}

async function searchMenu() {
    var texts = document.getElementById("inputsearchmenu").value
    document.getElementById("listproductsearch").style.display = 'block'
    if (texts.length == 0) {
        document.getElementById("listproductsearch").style.display = 'none'
        return;
    }
    var url = 'http://localhost:8080/api/product/public/search-by-param?page=0&size=50&search=' + texts;
    const response = await fetch(url, {});
    var result = await response.json();
    var list = result.content;
    var main = '';
    for (i = 0; i < list.length; i++) {
        main += `<a href="detail?id=${list[i].id}&name=${list[i].name}" class="tenspsearch"><div class="singlesearch col-md-12">
                    <div class="row">
                        <div class="col-2"><img class="imgprosearch" src="${list[i].imageBanner}"></div>
                        <div class="col-10">
                            <span class="tenspsearch">${list[i].name}</span><br>
                            <span class="tenspsearch">${formatmoney(list[i].price)}</span>
                        </div>
                    </div>
                </div></a>`
    }
    document.getElementById("listproductmn").innerHTML = main;
}
async function searchMenuMobile() {
    var texts = document.getElementById("inputsearchmobile").value
    if (texts.length == 0) {
        document.getElementById("listproductsearchmobile").innerHTML = '';
        return;
    }
    var url = 'http://localhost:8080/api/product/public/search-by-param?page=0&size=50&search=' + texts;
    const response = await fetch(url, {});
    var result = await response.json();
    var list = result.content;
    var main = '';
    for (i = 0; i < list.length; i++) {
        main += `<div class="singlesearch col-md-12">
                    <div class="p40"><a href="detail?id=${list[i].id}&name=${list[i].name}"><img class="imgprosearchp" src="${list[i].imageBanner}"></a></div>
                    <div class="p60">
                        <a href="detail?id=${list[i].id}&name=${list[i].alias}"><span class="tenspsearch">${list[i].name}</span><br>
                        <span class="tenspsearch">${formatmoney(list[i].price)}</span></a>
                    </div>
                </div>`
    }
    document.getElementById("listproductsearchmobile").innerHTML = main;
}

function loadFooter() {
    var foo = `<footer class="text-center text-lg-start text-muted">
    <section class="d-flex justify-content-center justify-content-lg-between p-4 border-bottom">
        <div class="me-5 d-none d-lg-block"><span>Theo dõi chúng tôi tại:</span></div>
        <div>
        <a href="" class="me-4 text-reset"><i class="fab fa-facebook-f"></i></a>
        <a href="" class="me-4 text-reset"><i class="fab fa-twitter"></i></a>
        <a href="" class="me-4 text-reset"><i class="fab fa-google"></i></a>
        <a href="" class="me-4 text-reset"><i class="fab fa-instagram"></i></a>
        <a href="" class="me-4 text-reset"><i class="fab fa-linkedin"></i></a>
        <a href="" class="me-4 text-reset"><i class="fab fa-github"></i></a>
        </div>
    </section>
    <section class="">
        <div class=" text-center text-md-start mt-5">
        <div class="row mt-3">
            <div class="col-md-2 col-lg-2 col-xl-2 mx-auto mb-4">
                <h6 class="text-uppercase fw-bold mb-4">TRỢ GIÚP</h6>
                <p><a href="#!" class="text-reset">Hướng dẫn mua hàng</a></p>
                <p><a href="#!" class="text-reset">Phương thức thanh toán</a></p>
                <p><a href="#!" class="text-reset">Phương thức vận chuyển</a></p>
                <p><a href="#!" class="text-reset">Chính sách đổi - trả</a></p>
                <p><a href="#!" class="text-reset">Chính sách bồi hoàn</a></p>
            </div>
            <div class="col-md-2 col-lg-2 col-xl-2 mx-auto mb-4">
            <h6 class="text-uppercase fw-bold mb-4">TÀI KHOẢN CỦA BẠN</h6>
            <p><a href="account" class="text-reset">Cập nhật tài khoản</a></p>
            <p><a href="cart" class="text-reset">Giỏ hàng</a></p>
            <p><a href="#!" class="text-reset">Lịch sử giao dịch</a></p>
            <p><a href="#!" class="text-reset">Sản phẩm yêu thích</a></p>
            <p><a href="#!" class="text-reset">Kiểm tra đơn hàng</a></p>
            </div>
            <div class="col-md-3 col-lg-2 col-xl-2 mx-auto mb-4">
            <h6 class="text-uppercase fw-bold mb-4">Dịch vụ</h6>
            <p><a href="#!" class="text-reset">24/7</a></p>
            <p><a href="#!" class="text-reset">free ship toàn quốc</a></p>
            <p><a href="#!" class="text-reset">lỗi 1 đổi 1</a></p>
            </div>
            <div class="col-md-4 col-lg-3 col-xl-3 mx-auto mb-md-0 mb-4">
            <h6 class="text-uppercase fw-bold mb-4">Liên hệ</h6>
            <p><i class="fas fa-home me-3"></i> Hà nội, Việt Nam</p>
            <p><i class="fas fa-envelope me-3"></i> Dtshop@gmail.com</p>
            <p><i class="fas fa-phone me-3"></i> 0338256771</p>
            </div>
        </div>
        </div>
    </section>
    </footer>`
    foo +=
        `<div class="modal fade" id="modalsearch" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-fullscreen-xxl-down modelreplay">
        <div class="modal-content">
            <div class="modal-header">
            <h5 class="modal-title" id="exampleModalLabel">Tìm kiếm sản phẩm</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="searchmenu searchsm">
                    <input id="inputsearchmobile" onkeyup="searchMenuMobile()" class="imputsearchmenu" placeholder="Tìm kiếm">
                    <button class="btnsearchmenu"><i class="fa fa-search"></i></button>
                </div>

                <div id="listproductsearchmobile" class="row">
                    <!----
                    <div class="singlesearch col-md-12">
                        <div class="p40"><a href=""><img class="imgprosearchp" src="image/pro.webp"></a></div>
                        <div class="p60">
                            <a href=""><span class="tenspsearch">Chân váy nữ dáng A</span><br>
                            <span class="tenspsearch">214.500đ</span></a>
                        </div>
                    </div>
                    ---->
                </div>
            </div>
        </div>
        </div>
    </div>`
    document.getElementById("footer").innerHTML = foo;
}

async function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    window.location.replace('login')
}


function formatmoney(money) {
    const VND = new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND',
    });
    return VND.format(money);
}

async function loadCartMenu() {
    if(token == null){
        return;
    }
    var url = 'http://localhost:8080/api/cart/user/count-cart' ;
    const response = await fetch(url, {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    if(response.status > 300){
        return;
    }
    var count = await response.text();
    document.getElementById("slcartmenusm").innerHTML = count
    document.getElementById("slcartmenu").innerHTML = count
}