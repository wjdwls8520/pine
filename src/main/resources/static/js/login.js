function naverLogin() {
    window.location.href = "/auth/naver/login";

}

function googleLogin(){
    window.location.href = "/oauth2/authorization/google";
}

function findPostCode(){

    document.getElementById('adSerchOverlay').style.display = 'block';

    new daum.Postcode({
        oncomplete: function(data) {
            let addr = (data.userSelectedType === 'R') ? data.roadAddress : data.jibunAddress;
            let extraAddr = '';
            if (data.userSelectedType === 'R') {
                if(data.bname) extraAddr += data.bname;
                if(data.buildingName) extraAddr += (extraAddr ? ', ' + data.buildingName : data.buildingName);
                if(extraAddr) addr += ' (' + extraAddr + ')';
            }

            document.getElementById('address_code').value = data.zonecode;
            document.getElementById('address_1').value = addr;
            document.getElementById('address_2').focus();

        },
        onclose: function() {
            document.getElementById('adSerchOverlay').style.display = 'none';
        }
    }).open();
}

function validJoin(){

    const privacyAgreed = document.querySelector('input[name="privacy_agreed"]');
    const termsAgreed = document.querySelector('input[name="terms_agreed"]');
    const marketingAgreed = document.querySelector('input[name="marketing_agreed"]');

    if (!privacyAgreed.checked) {
        alert('개인정보 약관에 동의해 주세요.');
        privacyAgreed.focus();
        return;
    }

    if (!termsAgreed.checked) {
        alert('정책 약관에 동의해 주세요.');
        termsAgreed.focus();
        return;
    }

    if(!marketingAgreed.checked){
        if(!confirm('동의하지 않으실 경우 알람 및 혜택을 받지 못할 수 있습니다. 계속하시겠습니까?')){
            marketingAgreed.focus();
            return;
        }
    }

    const form = document.querySelector("form.contract");

    if (!marketingAgreed.checked) {
        // unchecked 상태일 때 value를 false로
        if (!form.querySelector('input[name="marketing_agreed_hidden"]')) {
            const hiddenInput = document.createElement('input');
            hiddenInput.type = 'hidden';
            hiddenInput.name = 'marketing_agreed';
            hiddenInput.value = 'false';
            form.appendChild(hiddenInput);
        }
    } else {
        marketingAgreed.value = 'true';
    }

    form.submit();
}

function beforInsertMember(){

    const email = document.querySelector('input[name="email"]').value;
    const name = document.querySelector('input[name="name"]').value;
    const nickname = document.querySelector('input[name="nickname"]').value;
    const addressCode = document.querySelector('input[name="address_code"]').value;
    const address1 = document.querySelector('input[name="address_1"]').value;
    const phone = document.querySelector('input[name="phone"]').value.trim();
    const profile_msg = document.querySelector('input[name="profile_msg"]').value;

    const regPhone = /^0\d{1,2}-?\d{3,4}-?\d{4}$/;

    const form = document.querySelector("form.joinMember");


    if(!email){ alert('이메일이 비어있습니다.'); return; }
    if(!name){ alert('이름이 비어있습니다.'); return;}
    if(!phone){ alert('전화번호를 입력해주세요'); return; }
    if(!regPhone.test(phone)){ alert('올바른 전화번호가 아닙니다.'); return; }
    if(!nickname){ alert('닉네임을 입력해주세요'); return; }
    if(!addressCode){ alert('주소검색을 통해 주소를 입력해주세요'); return; }
    if(!address1){ alert('주소검색을 통해 주소를 입력해주세요'); return; }

    form.submit();

}

document.addEventListener("DOMContentLoaded", () => {
    searchContry();
});

function searchContry(){
    let timer;

    const input = document.getElementById("country");
    const list  = document.getElementById("country_result");

    input.addEventListener("input", e => {
        clearTimeout(timer);
        const keyword = e.target.value.trim();

        if (keyword.length < 1) {
            list.innerHTML = "";
            list.style.display = "none";
            return;
        }

        timer = setTimeout(() => {
            fetch(`/countrySearch?keyword=${encodeURIComponent(keyword)}`)
                .then(res => res.json())
                .then(data => {

                    list.innerHTML = "";

                    if (data.length === 0) {
                        list.style.display = "none";
                        return;
                    }

                    // console.log("data : "+ data.country_nm);

                    data.forEach(c => {
                        const li = document.createElement("li");
                        li.textContent = `${c.country_nm} (${c.country_eng_nm})(${c.country_iso_alp2})`;

                        li.onclick = e => {
                            e.stopPropagation();
                            input.value = c.country_nm + " " + c.country_eng_nm;
                            list.innerHTML = "";
                            list.style.display = "none";
                        };

                        list.appendChild(li);
                    });

                    list.style.display = "block";
                });
        }, 400);
    });

    document.addEventListener("click", e => {
        if (!e.target.closest(".country_auto_box")) {
            list.innerHTML = "";
            list.style.display = "none";
        }
    });
}

