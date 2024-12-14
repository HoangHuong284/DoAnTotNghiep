var size = 10;
async function loadProductIndex(page) {
    var url = 'http://localhost:8080/api/product/public/findAll-page?page=' + page + '&size=' + size;
    const response = await fetch(url, {
        method: 'GET'
    });
    var result = await response.json();
    console.log(result)
    var list = result.content;
    var totalPage = result.totalPages;

    var main = '';
    for (i = 0; i < list.length; i++) {
        var listimg = ''
        for (j = 0; j < list[i].productImages.length; j++) {
            listimg += `<div class="divimgsmpro"><img class="imgsmpro" src="${list[i].productImages[j].linkImage}"></div>`
        }
        main += `<div class="col-lg-20p col-md-3 col-sm-6 col-6">
                    <a href="detail?id=${list[i].id}&name=${list[i].name}" class="linkpro">
                        <div class="singlepro">
                            <div class="productsold"><span class="reviewsp">Đã bán: ${list[i].quantitySold}</span></div>
                            <img src="${list[i].imageBanner}" class="imgpro">
                            <span class="proname">${list[i].name}</span>
                            <span class="proprice">${formatmoney(list[i].price)} ${list[i].oldPrice==null?'':`<span class="oldprice">${formatmoney(list[i].oldPrice)}</span>`}</span>
                            <div class="listimgpro">${listimg}</div>
                        </div>
                    </a>
                </div>`
    }
    document.getElementById("listproductindex").innerHTML = main

    var mainpage = ''
    for (i = 1; i <= totalPage; i++) {
        mainpage += `<li onclick="loadProductIndex(${(Number(i) - 1)})" class="page-item"><a class="page-link" href="#listsp">${i}</a></li>`
    }
    document.getElementById("pageable").innerHTML = mainpage
}


async function loadSanPhamBanChay(page) {
    var url = 'http://localhost:8080/api/product/public/findAll-page?page=' + page + '&size=' + size+'&sort=quantitySold,desc';
    const response = await fetch(url, {
        method: 'GET'
    });
    var result = await response.json();
    console.log(result)
    var list = result.content;
    var totalPage = result.totalPages;

    var main = '';
    for (i = 0; i < list.length; i++) {
        var listimg = ''
        for (j = 0; j < list[i].productImages.length; j++) {
            listimg += `<div class="divimgsmpro"><img class="imgsmpro" src="${list[i].productImages[j].linkImage}"></div>`
        }
        main += `<div class="col-lg-20p col-md-3 col-sm-6 col-6">
                    <a href="detail?id=${list[i].id}&name=${list[i].name}" class="linkpro">
                        <div class="singlepro">
                            <div class="productsold"><span class="reviewsp">Đã bán: ${list[i].quantitySold}</span></div>
                            <img src="${list[i].imageBanner}" class="imgpro">
                            <span class="proname">${list[i].name}</span>
                            <span class="proprice">${formatmoney(list[i].price)} ${list[i].oldPrice==null?'':`<span class="oldprice">${formatmoney(list[i].oldPrice)}</span>`}</span>
                            <div class="listimgpro">${listimg}</div>
                        </div>
                    </a>
                </div>`
    }
    document.getElementById("listproductbanchay").innerHTML = main

    var mainpage = ''
    for (i = 1; i <= totalPage; i++) {
        mainpage += `<li onclick="loadSanPhamBanChay(${(Number(i) - 1)})" class="page-item"><a class="page-link pointer">${i}</a></li>`
    }
    document.getElementById("pageablebanchay").innerHTML = mainpage
}



async function loadAProduct() {
    var uls = new URL(document.URL)
    var id = uls.searchParams.get("id");
    if (id != null) {
        var url = 'http://localhost:8080/api/product/public/findById?id=' + id;
        const response = await fetch(url, {
            method: 'GET',
            headers: new Headers({
                'Authorization': 'Bearer ' + token
            })
        });
        var result = await response.json();
        document.getElementById("detailnamepro").innerHTML = result.name
        document.getElementById("codepro").innerHTML = result.code
        document.getElementById("quansale").innerHTML = 'Đã bán ' + result.quantitySold
        document.getElementById("pricedetail").innerHTML = formatmoney(result.price) 
        result.oldPrice == null ? '': document.getElementById("giacusp").innerHTML = formatmoney(result.oldPrice)
        document.getElementById("imgdetailpro").src = result.imageBanner
        document.getElementById("descriptiondetail").innerHTML = result.description

        document.getElementById("masp").innerHTML = result.code
        document.getElementById("tacgia").innerHTML = result.author
        document.getElementById("sotrang").innerHTML = result.numPage
        document.getElementById("nhasx").innerHTML = result.nxb
        document.getElementById("namsx").innerHTML = result.publishYear
        document.getElementById("khoiluong").innerHTML = result.weight+"(g)"
        document.getElementById("kichthuoc").innerHTML = result.size
        document.getElementById("hinhthuc").innerHTML = result.form
        document.getElementById("danhmuc").innerHTML = result.category.name

        var main = ''
        for (i = 0; i < result.productImages.length; i++) {
            main += `<div class="col-lg-2 col-md-2 col-sm-2 col-2 singdimg">
                        <img onclick="clickImgdetail(this)" src="${result.productImages[i].linkImage}" class="imgldetail">
                    </div>`
        }
        document.getElementById("listimgdetail").innerHTML = main

        
        var url = 'http://localhost:8080/api/product/public/search-full?page=0&size=4&sort=id,desc';
        var obj = {
            "categoryIds":[result.category.id],
            "minPrice":0,
            "maxPrice":100000000
        }
        const res = await fetch(url, {
            method: 'POST',
            headers: new Headers({
                'Content-Type': 'application/json'
            }),
            body: JSON.stringify(obj)
        });
        var result = await res.json();
        var list = result.content
        var main = ''
        for (i = 0; i < list.length; i++) {
            if (list[i].id == id) {
                continue;
            }
            var listimg = ''
            for (j = 0; j < list[i].productImages.length; j++) {
                listimg += `<div class="divimgsmpro"><img class="imgsmpro" src="${list[i].productImages[j].linkImage}"></div>`
            }
            main += `<div class="col-lg-6 col-md-6 col-sm-6 col-6">
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
        document.getElementById("listProductGy").innerHTML = main;
    }
}


async function clickImgdetail(e) {
    var img = document.getElementsByClassName("imgldetail");
    for (i = 0; i < img.length; i++) {
        document.getElementsByClassName("imgldetail")[i].classList.remove('imgactive');
    }
    e.classList.add('imgactive')
    document.getElementById("imgdetailpro").src = e.src
}

function clickSize(e, id, price) {
    var size = document.getElementsByClassName("radio-custom");
    for (i = 0; i < size.length; i++) {
        document.getElementsByClassName("radio-custom")[i].classList.remove('activesize');
    }
    e.classList.add('activesize')
    var inp = e.getElementsByTagName('input')[0]
    inp.checked = 'checked'
    document.getElementById("pricedetail").innerHTML = formatmoney(price)
    idSizeCart = id
}

function upAndDownDetail(val) {
    var quan = document.getElementById("inputslcart").value;
    if (val < 0 && quan == 1) {
        return;
    }
    document.getElementById("inputslcart").value = Number(quan) + Number(val)
}


var type = 1;


async function searchFull(page) {
    var min_price = document.getElementById("min_price").value;
    var max_price = document.getElementById("max_price").value;
    var sort = document.getElementById("sortpro").value;
    const trademarkCheckboxes = document.querySelectorAll('input[name="trademarkIds"]:checked');
    const trademarkIds = Array.from(trademarkCheckboxes).map(checkbox => checkbox.value);

    const categoryCheckboxes = document.querySelectorAll('input[name="categoryIds"]:checked');
    const categoryIds = Array.from(categoryCheckboxes).map(checkbox => checkbox.value);
    
    var url = 'http://localhost:8080/api/product/public/search-full?page=' + page + '&size=' + size+'&sort='+sort;
    var obj = {
        "categoryIds":categoryIds,
        "trademarkIds":trademarkIds,
        "minPrice":min_price,
        "maxPrice":max_price
    }
    console.log(obj);
    const response = await fetch(url, {
        method: 'POST',
        headers: new Headers({
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(obj)
    });
    var result = await response.json();
    console.log(result)
    var list = result.content;
    var totalPage = result.totalPages;

    var main = '';
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
    document.getElementById("listproductpro").innerHTML = main
    document.getElementById("slsp").innerHTML = result.totalElements

    var mainpage = ''
    for (i = 1; i <= totalPage; i++) {
        mainpage += `<li onclick="searchFull(${(Number(i) - 1)})" class="page-item"><a class="page-link" class="pointer">${i}</a></li>`
    }
    document.getElementById("pageable").innerHTML = mainpage
    $("#modalfilter").modal("hide")
}



