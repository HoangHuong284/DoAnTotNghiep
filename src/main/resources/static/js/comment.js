var star = 5;
async function loadProductComment() {
    if (token == null) {
        document.getElementById("mycomment").innerHTML = ''
    }
    var uls = new URL(document.URL)
    var id = uls.searchParams.get("id");
    if (id != null) {
        var url = 'http://localhost:8080/api/product-comment/public/find-by-product?idproduct=' + id;
        const response = await fetch(url, {
            method: 'GET',
            headers: new Headers({
                'Authorization': 'Bearer ' + token
            })
        });
        var list = await response.json();
        var main = ''
        for (i = 0; i < list.length; i++) {
            var star = '';
            for (j = 0; j < list[i].star; j++) {
                star += `<span class="fa fa-star checkedstar"></span>`
            }
            main += `<div class="singlectlct">
            <div class="row">
                <div class="col-11">
                    <div class="d-flex nguoidangctl">

                        <img class="avtuserdangctl" src="${list[i].user.avatar==null?'':list[i].user.avatar}">
                        <span class="usernamedangctl">${list[i].user.fullName==null?'Người dùng':list[i].user.fullName}</span>
                        <span class="ngaytraloi">${list[i].createdDate}</span>
                        <span class="starcmts">${star}</span>
                        ${list[i].isMyComment==true?`<span class="starcmts"><i onclick="deleteComment(${list[i].id})" class="fa fa-trash pointer"></i></span>`:''}
                    </div>
                    <div class="contentctlct">${list[i].content}</div>
                </div>
            </div>
        </div>`
        }
        document.getElementById("listcautlct").innerHTML = main
    }
}

function loadStar(val) {
    star = val + 1;
    var listS = document.getElementById("liststar").getElementsByClassName("fa-star");
    for (i = 0; i < listS.length; i++) {
        listS[i].classList.remove('checkedstar');
    }
    for (i = 0; i < listS.length; i++) {
        if (i <= val) {
            listS[i].classList.add('checkedstar');
        }
    }

}

async function uploadMultipleFile(fileInput) {
    const formData = new FormData()
    for (i = 0; i < fileInput.files.length; i++) {
        formData.append("file", fileInput.files[i])
    }
    var urlUpload = 'http://localhost:8080/api/public/upload-multiple-file';
    const res = await fetch(urlUpload, {
        method: 'POST',
        body: formData
    });
    if(res.status < 300){
        return await res.json();
    }
    else{
        return [];
    }
}

async function saveComment() {
    var uls = new URL(document.URL)
    var id = uls.searchParams.get("id");
    var url = 'http://localhost:8080/api/product-comment/user/create';
    var noidungbl = document.getElementById("noidungbl").value
    var comment = {
        "star": star,
        "content": noidungbl,
        "product": {
            "id": id
        }
    }
    const response = await fetch(url, {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(comment)
    });
    if (response.status < 300) {
    document.getElementById("noidungbl").value='';
        swal({
                title: "Thông báo",
                text: "Đã đăng bình luận của bạn",
                type: "success"
            },
            function() {
                loadProductComment();
            });
    } 
    else {
        if (response.status == exceptionCode) {
            var result = await response.json()
            toastr.error(result.defaultMessage);
        }
        else{
            toastr.error("Thất bại!");
        }
    }
}

async function deleteComment(id) {
    var con = confirm("Bạn muốn xóa bình luận này?");
    if (con == false) {
        return;
    }
    var url = 'http://localhost:8080/api/product-comment/all/delete?id=' + id;
    const response = await fetch(url, {
        method: 'DELETE',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    if (response.status < 300) {
        toastr.success("xóa thành công!");
        await new Promise(r => setTimeout(r, 1000));
        loadProductComment();
    }
    if (response.status == exceptionCode) {
        var result = await response.json()
        toastr.warning(result.defaultMessage);
    }
}