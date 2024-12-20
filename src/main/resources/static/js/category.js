async function loadCategoryIndex() {
    var url = 'http://localhost:8080/api/category/public/findAllList';
    const response = await fetch(url, {});
    var list = await response.json();
    console.log(list)
    var main = `<div class="listdmindex owl-2-style"><div class="owl-carousel owl-2" id="listcategoryindex">`
    for (i = 0; i < list.length; i++) {
        main += `<div class="media-29101">
                    <a href="product?category=${list[i].id}"><img src="${list[i].imageBanner}" alt="Image" class="img-fluid"></a>
                    <h3><a href="product?category=${list[i].id}">${list[i].name}</a></h3>
                </div>`;
    }
    main += `</div></div>`
    document.getElementById("dsmh_index").innerHTML = main;
    loadCou();
}

async function outstandingCategory() {
    var url = 'http://localhost:8080/api/category/public/outstanding';
    const response = await fetch(url, {});
    var list = await response.json();
    console.log(list)
    var main = ``
    for (i = 0; i < list.length; i++) {
        main += `<div class="col-lg-3 col-md-3 col-sm-6 col-6">
                    <a href="product?category=${list[i].id}">
                        <div class="singledmpro">
                            <img src="${list[i].imageBanner}" class="imgcatepro">
                            <span class="namedmpro">${list[i].name}</span>
                        </div>
                    </a>
                </div>`;
    }
    document.getElementById("listdmproduct").innerHTML = main;
}


async function loadCategorySub() {
    var uls = new URL(document.URL)
    var danhmuc = uls.searchParams.get("danhmuc");
    var url = 'http://localhost:8080/api/category/public/all-category';
    const response = await fetch(url, {});
    var list = await response.json();
    var main = ''
    for (i = 0; i < list.length; i++) {
        main += `<div class="singlelistmenu">
                <label class="checkbox-custom cateparent">${list[i].name}</i>
                    <input value="${list[i].id}" ${danhmuc == list[i].id?'checked':''} name="categoryIds" class="inputcheck" type="checkbox">
                    <span class="checkmark-checkbox"></span>
                </label>
            </div>`
    }
    document.getElementById("listsearchCategory").innerHTML = main;
}
async function loadThuongHieuSub() {
    var url = 'http://localhost:8080/api/trademark/public/all-trademark';
    const response = await fetch(url, {});
    var list = await response.json();
    var main = ''
    for (i = 0; i < list.length; i++) {
        main += `<div class="singlelistmenu">
                <label class="checkbox-custom cateparent">${list[i].name}</i>
                    <input value="${list[i].id}" name="trademarkIds" class="thuonghieucheck" type="checkbox">
                    <span class="checkmark-checkbox"></span>
                </label>
            </div>`
    }
    document.getElementById("listthuonghieu").innerHTML = main;
}
