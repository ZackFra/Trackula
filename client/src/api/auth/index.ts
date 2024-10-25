const login = (loginRequest: LoginRequest) => fetch('/spring/login', {
    method: 'POST',
    credentials: 'include',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify(loginRequest)
})

export { login }