function getCategory() {
    fetch("/api/group/gCategory")
        .then(response => {
            if (!response.ok) throw new Error("네트워크 응답 실패");
            return response.json(); // axios는 자동으로 json 변환
        })
        .then(data => {
            console.log(data);
        })
        .catch(err => console.error(err));
}