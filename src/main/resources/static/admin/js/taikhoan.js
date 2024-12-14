async function loadAllUser() {
    $('#example').DataTable().destroy();
    var url = 'http://localhost:8080/api/user/admin/get-user-by-role';
    var role = document.getElementById("role").value
    if (role != "") {
        url += '?role=' + role
    }
    const response = await fetch(url, {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    var listUser = await response.json();
    console.log();
    var main = '';
    for (i = 0; i < listUser.length; i++) {
        var btn = '';
        if (listUser[i].actived == 0) {
            var btn = `<td class="sticky-col"><button onclick="lockOrUnlock(${listUser[i].id},0)" class="btn btn-danger"><i class="fa fa-unlock"></i> mở khóa</button></td>`
        } else {
            var btn = `<td class="sticky-col"><button onclick="lockOrUnlock(${listUser[i].id},1)" class="btn btn-primary"><i class="fa fa-lock"></i> Khóa</button></td>`
        }
        if (listUser[i].authorities.name == "ROLE_ADMIN") {
            btn = '<td class="sticky-col"></td>'
        }
        main += `<tr>
                    <td>${listUser[i].id}</td>
                    <td><img src="${listUser[i].avatar == null?'':listUser[i].avatar}" class="imgtable"></td>
                    <td>${listUser[i].email} ${listUser[i].userType=="GOOGLE"?'<img src="../image/google.png" class="googleicon">':''}</td>
                    <td>${listUser[i].fullName}</td>
                    <td>${listUser[i].createdDate}</td>
                    <td>${listUser[i].authorities.name} <i onclick="setRole(${listUser[i].id}, '${listUser[i].authorities.name}')" data-bs-toggle="modal" data-bs-target="#modalrole" class="fa fa-edit iconaction"></i></td>
                    ${btn}
                </tr>`
    }
    document.getElementById("listuser").innerHTML = main
    $('#example').DataTable();
}

async function lockOrUnlock(id, type) {
    var con = confirm("Xác nhận hành động?");
    if (con == false) {
        return;
    }
    var url = 'http://localhost:8080/api/user/admin/lockOrUnlockUser?id=' + id;
    const response = await fetch(url, {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    if (response.status < 300) {
        var mess = '';
        if (type == 1) {
            mess = 'Khóa thành công'
        } else {
            mess = 'Mở khóa thành công'
        }
        swal({
                title: "Thông báo",
                text: mess,
                type: "success"
            },
            function() {
                window.location.reload();
            });
    } else {
        swal({
                title: "Thông báo",
                text: "hành động thất bại",
                type: "error"
            },
            function() {
                window.location.reload();
            });
    }
}


async function addAdmin() {
    var url = 'http://localhost:8080/api/admin/addaccount'
    var fullname = document.getElementById("fullname").value
    var phone = document.getElementById("phone").value
    var email = document.getElementById("email").value
    var password = document.getElementById("pass").value
    var repassword = document.getElementById("repass").value
    var user = {
        "fullname": fullname,
        "phone": phone,
        "email": email,
        "password": password

    }
    if (password != repassword) {
        alert("Mật khẩu không trùng khớp")
        return;
    }
    const res = await fetch(url, {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(user)
    });
    var result = await res.json();
    if (res.status < 300) {
        swal({
                title: "Thông báo",
                text: "Tạo tài khoản thành công!",
                type: "success"
            },
            function() {
                window.location.reload();
            });
    }
    if (res.status == exceptionCode) {
        toastr.warning(result.defaultMessage);
    }
}

function setRole(id, role){
    document.getElementById("idacc").value = id
    document.getElementById("rolechange").value = role
}

async function loadAllAuthority() {
    var url = 'http://localhost:8080/api/authority/admin/all';
    const response = await fetch(url, {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    var list = await response.json();
    var main = '';
    for (i = 0; i < list.length; i++) {
        main += `<option value="${list[i].name}">${list[i].name}</option>`
    }
    document.getElementById("rolechange").innerHTML = main
}


async function changerole() {
    var id = document.getElementById("idacc").value
    var role = document.getElementById("rolechange").value
    var url = 'http://localhost:8080/api/user/admin/change-role?id=' + id+'&role='+role;
    const response = await fetch(url, {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    if (response.status < 300) {
        toastr.success("Thành công!");
        loadAllUser();
    } else {
        toastr.error("Thất bại");
    }
}