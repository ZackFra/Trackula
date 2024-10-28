const login = async (data: LoginRequest) => {
    const request =  {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(data),  
    }
    console.log(request)
    return fetch("/api/perform_login", request)
}

export { login }