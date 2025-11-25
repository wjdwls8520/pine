async function naverLogin() {
    const res = await fetch("/auth/naver/login");
    const url = await res.text();

    console.log(url);

}