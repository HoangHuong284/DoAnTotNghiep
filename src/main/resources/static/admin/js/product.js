const listFile = [];



async function loadProduct() {
    $('#example').DataTable().destroy();
    var url = 'http://localhost:8080/api/product/public/findAll-list';
    const response = await fetch(url, {
        method: 'GET'
    });
    var list = await response.json();
    console.log(list);
    var main = '';
    for (i = 0; i < list.length; i++) {
        main += `<tr>
                    <td>${list[i].id}</td>
                    <td><img src="${list[i].imageBanner}" style="width: 100px;"></td>
                    <td>${list[i].code}</td>
                    <td>${list[i].category.name}</td>
                    <td>${list[i].name}</td>
                    <td>${formatmoney(list[i].price)}</td>
                    <td>${list[i].createdDate}</td>
                    <td>${list[i].createdDate}</td>
                    <td>${list[i].quantitySold}</td>
                    <td>${list[i].quantity}</td>

                    <td class="sticky-col">
                        <i onclick="deleteProduct(${list[i].id})" class="fa fa-trash-alt iconaction"></i>
                        <a href="addproduct?id=${list[i].id}"><i class="fa fa-edit iconaction"></i><br></a>
                    </td>
                </tr>`
    }
    document.getElementById("listproduct").innerHTML = main
    $('#example').DataTable(
    {
                    "columnDefs": [
                        { "targets": [0,1, 6, 7, 8, 9], "searchable": false } // Tắt tìm kiếm cho các cột Ảnh, Ngày tạo, Số lượng bán, Số lượng tồn kho, Hành động
                    ]
                }
    );
}

async function loadAProduct() {
    var uls = new URL(document.URL)
    var id = uls.searchParams.get("id");
    if (id != null) {
        document.getElementById("btnaddpro").innerHTML = `<i class="fa fa-edit"></i> Cập nhật sản phẩm`
        var url = 'http://localhost:8080/api/product/public/findById?id=' + id;
        const response = await fetch(url, {
            method: 'GET'
        });
        var result = await response.json();
        console.log(result)
        document.getElementById("tensp").value = result.name
        document.getElementById("price").value = result.price
        document.getElementById("oldprice").value = result.oldPrice
        linkbanner = result.imageBanner
        document.getElementById("imgpreview").src = result.imageBanner
        tinyMCE.get('editor').setContent(result.description)
        document.getElementById("listcategory").value = result.category.id
        document.getElementById("masp").value = result.code

        document.getElementById("tacgia").value = result.author
        document.getElementById("soluong").value = result.quantity
        document.getElementById("hinhthuc").value = result.form
        document.getElementById("kichthuoc").value = result.size
        document.getElementById("nxb").value = result.nxb
        document.getElementById("namxb").value = result.publishYear
        document.getElementById("khoiluong").value = result.weight
        document.getElementById("sotrang").value = result.numPage
        var main = ''
        for (i = 0; i < result.productImages.length; i++) {
            main += `<div id="imgdathem${result.productImages[i].id}" class="col-md-2 col-sm-4 col-6">
                        <img src="${result.productImages[i].linkImage}" class="image-uploaded">
                        <button onclick="deleteProductImage(${result.productImages[i].id})" class="btn btn-danger form-control">Xóa ảnh</button>
                    </div>`
        }
        document.getElementById("preview").innerHTML = main
        

    }
}


var linkbanner = '';
async function saveProduct() {
function validateForm() {
        let isValid = true;
        const requiredFields = [
            { id: "tensp", message: "Tên sản phẩm không được để trống." },
            { id: "masp", message: "Mã sản phẩm không được để trống." },
            { id: "listcategory", message: "Vui lòng chọn danh mục." },
            { id: "tacgia", message: "Tác giả không được để trống." },
            { id: "price", message: "Giá tiền hiện tại không được để trống." },
            { id: "soluong", message: "Số lượng không được để trống." },
            { id: "hinhthuc", message: "Hình thức không được để trống." },
            { id: "kichthuoc", message: "Kích thước không được để trống." },
            { id: "nxb", message: "Nhà xuất bản không được để trống." },
            { id: "namxb", message: "Năm xuất bản không được để trống." },
            { id: "khoiluong", message: "Khối lượng không được để trống." },
            { id: "sotrang", message: "Số trang không được để trống." }
        ];

        requiredFields.forEach(field => {
            const element = document.getElementById(field.id);
            if (!element.value.trim()) {
                isValid = false;
                toastr.error(field.message); // Hiển thị lỗi
                element.focus(); // Đưa con trỏ đến trường lỗi đầu tiên
            }
        });

        return isValid;
    }

    // Kiểm tra dữ liệu nhập trước khi gửi yêu cầu
    if (!validateForm()) {
        return;
    }

    var uls = new URL(document.URL)
    var id = uls.searchParams.get("id");
    var url = 'http://localhost:8080/api/product/admin/create';
    if (id != null) {
        url = 'http://localhost:8080/api/product/admin/update';
    }
    document.getElementById("loading").style.display = 'block'
    await uploadFile(document.getElementById("anhdaidien"));
    var listLinkImg = await uploadMultipleFile();
    var product = {
        "id": id,
        "code": document.getElementById("masp").value,
        "name": document.getElementById("tensp").value,
        "imageBanner":linkbanner,
        "oldPrice": document.getElementById("oldprice").value,
        "price": document.getElementById("price").value,
        "quantity": document.getElementById("soluong").value,
        "author": document.getElementById("tacgia").value,
        "nxb": document.getElementById("nxb").value,
        "publishYear": document.getElementById("namxb").value,
        "weight": document.getElementById("khoiluong").value,
        "size": document.getElementById("kichthuoc").value,
        "numPage": document.getElementById("sotrang").value,
        "form": document.getElementById("hinhthuc").value,
        "description": tinyMCE.get('editor').getContent(),
        "category": {"id":document.getElementById("listcategory").value},
        "productImages": listLinkImg,
    }
    console.log(product)
    const response = await fetch(url, {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(product)
    });
    var result = await response.json();
    console.log(result)

    if (response.status < 300) {
        swal({ title: "Thông báo", text: "thêm/sửa sản phẩm thành công", type: "success" },
            function() { window.location.href = 'product' });
    } else {
        swal({title: "Thông báo",text: "thêm/sửa sản phẩm thất bại",type: "error"},
            function() { document.getElementById("loading").style.display = 'none' });
    }
}


function loadInit() {
    $('input#choosefile').change(function() {
        var files = $(this)[0].files;
    });
    document.querySelector('#choosefile').addEventListener("change", previewImages);

    function previewImages() {
        var files = $(this)[0].files;
        for (i = 0; i < files.length; i++) {
            listFile.push(files[i]);
        }

        var preview = document.querySelector('#preview');

        for (i = 0; i < files.length; i++) {
            readAndPreview(files[i]);
        }

        function readAndPreview(file) {

            var reader = new FileReader(file);

            reader.addEventListener("load", function() {
                var div = document.createElement('div');
                div.className = 'col-lg-2 col-md-3 col-sm-6 col-6';
                div.style.height = '120px';
                div.style.paddingTop = '5px';
                div.marginTop = '100px';
                preview.appendChild(div);

                var img = document.createElement('img');
                img.src = this.result;
                img.style.height = '85px';
                img.style.width = '90%';
                img.className = 'image-upload';
                img.style.marginTop = '5px';
                div.appendChild(img);

                var button = document.createElement('button');
                button.style.height = '30px';
                button.style.width = '90%';
                button.innerHTML = 'xóa'
                button.className = 'btn btn-warning';
                div.appendChild(button);

                button.addEventListener("click", function() {
                    div.remove();
                    console.log(listFile.length)
                    for (i = 0; i < listFile.length; i++) {
                        if (listFile[i] === file) {
                            listFile.splice(i, 1);
                        }
                    }
                    console.log(listFile.length)
                });
            });

            reader.readAsDataURL(file);

        }

    }

}


async function uploadMultipleFile() {
    const formData = new FormData()
    for (i = 0; i < listFile.length; i++) {
        formData.append("file", listFile[i])
    }
    var urlUpload = 'http://localhost:8080/api/public/upload-multiple-file';
    const res = await fetch(urlUpload, {
        method: 'POST',
        body: formData
    });
    if (res.status < 300) {
        var arr = await res.json();
        var listrt = [];
        for(i=0; i<arr.length; i++){
            var oj = {
                "linkImage":arr[i]
            }
            listrt.push(oj)
        }
        return listrt
    } else {
        return [];
    }
}

async function uploadFile(filePath) {
    const formData = new FormData()
    formData.append("file", filePath.files[0])
    var urlUpload = 'http://localhost:8080/api/public/upload-file';
    const res = await fetch(urlUpload, {
        method: 'POST',
        body: formData
    });
    if (res.status < 300) {
        linkbanner = await res.text();
    }
}



async function deleteProductImage(id) {
    var con = confirm("Bạn chắc chắn muốn xóa ảnh sản phẩm này?");
    if (con == false) {
        return;
    }
    var url = 'http://localhost:8080/api/product-image/admin/delete?id=' + id;
    const response = await fetch(url, {
        method: 'DELETE',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    if (response.status < 300) {
        toastr.success("xóa ảnh sản phẩm thành công!");
        document.getElementById("imgdathem"+id).style.display = 'none'
    }
}

async function deleteProduct(id) {
    var con = confirm("Bạn chắc chắn muốn xóa sản phẩm này?");
    if (con == false) {
        return;
    }
    var url = 'http://localhost:8080/api/product/admin/delete?id=' + id;
    const response = await fetch(url, {
        method: 'DELETE',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    if (response.status < 300) {
        toastr.success("xóa sản phẩm thành công!");
        loadProduct()
    }
    if (response.status == exceptionCode) {
        var result = await response.json()
        toastr.warning(result.defaultMessage);
    }
}




async function xuatExcel() {
    var url = 'http://localhost:8080/api/product/admin/export-excel';
    const response = await fetch(url, {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    var list = await response.json();
    const workbook = XLSX.utils.book_new();

    for (i = 0; i < list.length; i++) {
        // Tạo dữ liệu cho bảng tính
        let worksheet_data = [
            ["Ngày "+currentDate()],
            ["Thời gian: "+currentTime()],
            ["ID", "Mã sản phẩm", "Tên sản phẩm", "Giá bán", "Giá cũ", "Ngày tạo", "Danh mục", "Số lượng bán", "Số lượng tồn"], // Row 8
        ];
        let worksheet = XLSX.utils.aoa_to_sheet(worksheet_data);

        worksheet['!cols'] = [
            { wch: 6 }, { wch: 16.11 },{ wch: 52 },{ wch: 13.56 },{ wch: 13.56 },{ wch: 20 },{ wch: 15.11 },{ wch: 15.44 },{ wch: 15 },{ wch: 15 }
        ];

        // Định nghĩa kiểu đường viền
        const borderStyle = {
            top: { style: "thin", color: { rgb: "000000" } },
            bottom: { style: "thin", color: { rgb: "000000" } },
            left: { style: "thin", color: { rgb: "000000" } },
            right: { style: "thin", color: { rgb: "000000" } }
        };
        const range = XLSX.utils.decode_range('A3:J3');
        for (let R = range.s.r; R <= range.e.r; ++R) {
            for (let C = range.s.c; C <= range.e.c; ++C) {
                const cell_address = { c: C, r: R };
                const cell_ref = XLSX.utils.encode_cell(cell_address);

                if (!worksheet[cell_ref]) worksheet[cell_ref] = {};
                worksheet[cell_ref].s = {
                    border: borderStyle,
                    alignment: { vertical: "center", horizontal: "center",wrapText:true },
                    font: { bold: true, sz: 12}
                };
            }
        }

        // const dataToAdd = list[i].products.map(item => [item.id, item.code, item.name, formatmoney(item.price), formatmoney(item.oldPrice),
        //     item.createdDate, item.category.name, item.trademark.name, item.quantitySold, item.quantitySold
        // ]);
        const dataToAdd = [];
        for(j=0; j<list[i].products.length; j++){
            var item = list[i].products[j]
            var pro = [item.id, item.code, item.name, formatmoney(item.price), formatmoney(item.oldPrice), item.createdDate, item.category.name, item.quantitySold, item.quantity];
            dataToAdd.push(pro)
        }

        XLSX.utils.sheet_add_aoa(worksheet, dataToAdd, { origin: 'A4' });
        XLSX.utils.book_append_sheet(workbook, worksheet, list[i].name);
    }
    XLSX.writeFile(workbook, 'data.XLSX');
    console.log('Dữ liệu đã được xuất ra file data.XLSX');
}


function currentDate(){
    const currentDate = new Date();
    const currentYear = currentDate.getFullYear();
    const currentMonth = String(currentDate.getMonth() + 1).padStart(2, '0');
    const currentDay = String(currentDate.getDate()).padStart(2, '0');

    const formattedDate = `${currentYear}-${currentMonth}-${currentDay}`;
    return formattedDate;
}

function currentTime(){
    const currentDate = new Date();
    const currentHours = String(currentDate.getHours()).padStart(2, '0');
    const currentMinutes = String(currentDate.getMinutes()).padStart(2, '0');

    const currentTime = `${currentHours}:${currentMinutes}`;
    return currentTime;
}