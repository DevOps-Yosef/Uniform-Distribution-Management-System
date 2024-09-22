import { NextRequest, NextResponse } from 'next/server';
import CryptoJS from 'crypto-js';


let udmsPaths = [];
udmsPaths.push('/admin');
udmsPaths.push('/employees');
udmsPaths.push('/uniforms');
udmsPaths.push('/positions');
udmsPaths.push('/logout');
udmsPaths.push('/home');
udmsPaths.push('/services');

// const redirectURIApp = "http://localhost:8083";
const redirectURIApp = "https://aps3.zemenbank.com/TestUDMS";
// const redirectURIApp = "https://aps3.zemenbank.com/UDMS";
// const authURI = "https://auth.zemenbank.com";
const authURI = "https://aps3.zemenbank.com/TestUDMS";
// const authURI = "https://aps3.zemenbank.com/UDMS";

async function validateToken(accessToken) {
    // console.log("validateToken (token)====== " + accessToken);
    let status = false;
    const options: RequestInit = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            Authorization: `Bearer ${accessToken}`
        },
        body: new URLSearchParams({
            token: accessToken,
            client_id: 'TestUDMS',
            // client_id: 'UDMS',
            client_secret: 'Za2d9E295WZ5p6qrnN9ZtWKJEbxDyB2z'
            // client_secret: '0vBUb8AoC088AO4s5RzpOvNofIkDXgb4'
        }),
        cache: 'no-store'
    };
    const response = await fetch('https://auth.zemenbank.com/auth/realms/test/protocol/openid-connect/token/introspect', options);
    if (response.ok) {
        const data = await response.json();
        if (data && data.active) {
            status = true;
        }
    }
    console.log("Validate Token === " + status);
    return status;
}

async function getKeycloakToken(authorizationCode) {
    const params = new URLSearchParams();
    params.append('grant_type', 'authorization_code');
    params.append('client_id', 'TestUDMS');
    // params.append('client_id', 'UDMS');
    params.append('client_secret', 'Za2d9E295WZ5p6qrnN9ZtWKJEbxDyB2z');
    // params.append('client_secret', '0vBUb8AoC088AO4s5RzpOvNofIkDXgb4');
    params.append('code', authorizationCode);
    params.append('redirect_uri', redirectURIApp);

    const options: RequestInit = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params,
        cache: 'no-store'
    };

    const response = await fetch('https://auth.zemenbank.com/auth/realms/test/protocol/openid-connect/token', options);
    if (response.ok) {
        const data = await response.json();
        if (data && data.access_token) {
            const userDetails: User = {
                name: '',
                role: '',
                token: data.access_token,
                refreshToken: data.refresh_token,
                active: true
            };
            return userDetails;
        }
    }
    const userDetails: User = {
        name: '',
        role: '',
        token: '',
        refreshToken: '',
        active: false
    };
    return userDetails;
}

async function refreshToken(refreshToken) {
    const params = new URLSearchParams();
    params.append('grant_type', 'refresh_token');
    params.append('refresh_token', refreshToken);
    params.append('client_id', 'TestUDMS');
    // params.append('client_id', 'UDMS');
    params.append('client_secret', 'Za2d9E295WZ5p6qrnN9ZtWKJEbxDyB2z');
    // params.append('client_secret', '0vBUb8AoC088AO4s5RzpOvNofIkDXgb4');

    const options: RequestInit = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params,
        cache: 'no-store'
    };

    const response = await fetch('https://auth.zemenbank.com/auth/realms/test/protocol/openid-connect/token', options);
    if (response.ok) {
        const data = await response.json();
        if (data && data.access_token) {
            const userDetails: User = {
                name: '',
                role: '',
                token: data.access_token,
                refreshToken: data.refresh_token,
                active: true
            };
            console.log("Refresh Token SUCCESS");
            return userDetails;
        }
    }
    const userDetails: User = {
        name: '',
        role: '',
        token: '',
        refreshToken: '',
        active: false
    };
    console.log("Refresh Token FAILS");
    return userDetails;
}


export async function getUserDetails(token) {
    const options: RequestInit = {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        },
        cache: 'no-store'
    };
    const response = await fetch('https://auth.zemenbank.com/auth/realms/test/protocol/openid-connect/userinfo', options);
    if (response.ok) {
        const roleResponse = await response.json();
        if (roleResponse) {
            //????????????????????????????????????????????????????????????????????????????????????????
            if (roleResponse && roleResponse.resource_access && roleResponse.resource_access.TestUDMS)  {
                // if (roleResponse && roleResponse.resource_access && roleResponse.resource_access.UDMS)  {
                    if (roleResponse.resource_access.TestUDMS.roles[0]) {
                        // if (roleResponse.resource_access.UDMS.roles[0]) {
                            const userDetails: User = {
                        name: roleResponse.name,
                        role: roleResponse.resource_access.TestUDMS?.roles[0],
                        // role: roleResponse.resource_access.UDMS.roles[0],
                        token: '',
                        refreshToken: '',
                        active: false
                    };
                    return userDetails;
                }
            }
            //????????????????????????????????????????????????????????????????????????????????????????
        }
    }
    const userDetails: User = {
        name: "",
        role: "",
        token: '',
        refreshToken: '',
        active: false
    };
    return userDetails;
}

export default async function middleware(request: NextRequest) {
    // console.log("------------------------------------------------------------------------------------------------------------------");
    if (udmsPaths.some(prefix => request.nextUrl.pathname.startsWith(prefix))
        || request.nextUrl.pathname.toString() === '/') {
        let currentDate = new Date();
        console.log(currentDate.toString());
        console.log("URL === " + request.nextUrl.pathname);
        console.log("URL === " + request.nextUrl.toString());
        if (request.nextUrl.pathname.toString() === '/logout' ||
            (request.nextUrl.pathname.toString() === '/' && !request?.nextUrl?.searchParams?.get("code"))) {
            console.log("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG -1");
            const response = NextResponse.next();
            const defaultOrigin = 'https://aps3.zemenbank.com';
            const defaultDomain = 'aps3.zemenbank.com';
            const origin = request.headers.get('origin') ?? '';
            if (origin === defaultOrigin) {
                response.headers.set('Access-Control-Allow-Origin', defaultDomain);
                response.headers.set('x-forwarded-host', defaultDomain);
                response.headers.set('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
                response.headers.set('Access-Control-Allow-Headers', 'Content-Type, Authorization');
            }
            return response;
        }
        try {
            const cookie = request.cookies.get('appSession.4')?.value;
            const secretKey = process.env.SECRET_KEY;
            const code = request?.nextUrl?.searchParams?.get("code");
            if (cookie && !code) {
                const bytes = CryptoJS.AES.decrypt(cookie, secretKey);
                const session = bytes.toString(CryptoJS.enc.Utf8);
                if (session) {
                    const tokenStatus = await validateToken(session);
                    if (tokenStatus) {
                        console.log("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG -3");
                        const response = NextResponse.next();
                        const defaultOrigin = 'https://aps3.zemenbank.com';
                        const defaultDomain = 'aps3.zemenbank.com';
                        const origin = request.headers.get('origin') ?? '';
                        if (origin === defaultOrigin) {
                            response.headers.set('Access-Control-Allow-Origin', defaultDomain);
                            response.headers.set('x-forwarded-host', defaultDomain);
                            response.headers.set('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
                            response.headers.set('Access-Control-Allow-Headers', 'Content-Type, Authorization');
                        }
                        return response;
                    }
                    else {
                        console.log("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG -333333333");
                        // const refreshCookie = cookies().get('appSession.2')?.value;
                        let refreshTokenFails: boolean = false;
                        const refreshCookie = request.cookies.get('appSession.2')?.value;
                        if (refreshCookie) {
                            const bytes = CryptoJS.AES.decrypt(refreshCookie, secretKey);
                            const refreshSession = bytes.toString(CryptoJS.enc.Utf8);
                            if (refreshSession) {
                                const tokenDataObject = await refreshToken(refreshSession);
                                console.log("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ-11");
                                console.dir(tokenDataObject);
                                if (tokenDataObject && tokenDataObject.active) {
                                    console.log("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ-22");
                                    refreshTokenFails = false;
                                    const payload = tokenDataObject.token;
                                    const refreskTokenPayload = tokenDataObject.refreshToken;
                                    const encryptedToken = CryptoJS.AES.encrypt(payload, secretKey).toString();
                                    const encryptedRefreshToken = CryptoJS.AES.encrypt(refreskTokenPayload, secretKey).toString();
                                    request.cookies.clear();
                                    const response = NextResponse.next();
                                    response.cookies.set('appSession.4', encryptedToken, {
                                        path: '/',
                                        httpOnly: true,
                                        sameSite: 'strict',
                                        secure: true,
                                    });
                                    response.cookies.set('appSession.2', encryptedRefreshToken, {
                                        path: '/',
                                        httpOnly: true,
                                        sameSite: 'strict',
                                        secure: true,
                                    });
                                    let currentUser: User = await getUserDetails(payload);
                                    const encryptedRole = CryptoJS.AES.encrypt(currentUser.role, secretKey).toString();
                                    const encryptedUserFullName = CryptoJS.AES.encrypt(currentUser.name, secretKey).toString();
                                    response.cookies.set('appSession.1', encryptedRole, {
                                        path: '/',
                                        httpOnly: true,
                                        sameSite: 'strict',
                                        secure: true,
                                    });
                                    response.cookies.set('appSession.6', encryptedUserFullName, {
                                        path: '/',
                                        httpOnly: true,
                                        sameSite: 'strict',
                                        secure: true,
                                    });
                                    const defaultOrigin = 'https://aps3.zemenbank.com';
                                    const defaultDomain = 'aps3.zemenbank.com';
                                    const origin = request.headers.get('origin') ?? '';
                                    if (origin === defaultOrigin) {
                                        response.headers.set('Access-Control-Allow-Origin', defaultDomain);
                                        response.headers.set('x-forwarded-host', defaultDomain);
                                        response.headers.set('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
                                        response.headers.set('Access-Control-Allow-Headers', 'Content-Type, Authorization');
                                    }
                                    return response;
                                }
                                else {
                                    refreshTokenFails = true;
                                }
                            }
                            else {
                                refreshTokenFails = true;
                            }
                        }
                        else {
                            refreshTokenFails = true;
                        }
                        if (refreshTokenFails) {
                            request.cookies.clear();
                            const loginUrl = new URL(authURI + '/auth/realms/test/protocol/openid-connect/auth', request.url);
                            loginUrl.searchParams.set('client_id', "TestUDMS");
                            // loginUrl.searchParams.set('client_id', "UDMS");
                            loginUrl.searchParams.set('redirect_uri', redirectURIApp);
                            loginUrl.searchParams.set('response_type', "code");
                            loginUrl.searchParams.set('scope', "openid offline_access");
                            return NextResponse.redirect(loginUrl);
                        }
                    }
                }
            }
            else {
                if (code) {
                    request.cookies.clear();
                    const tokenDataObject = await getKeycloakToken(code);
                    if (tokenDataObject && tokenDataObject.active) {
                        const payload = tokenDataObject.token;
                        const refreskTokenPayload = tokenDataObject.refreshToken;
                        const encryptedToken = CryptoJS.AES.encrypt(payload, secretKey).toString();
                        const encryptedRefreshToken = CryptoJS.AES.encrypt(refreskTokenPayload, secretKey).toString();
                        const response = NextResponse.next();
                        response.cookies.set('appSession.4', encryptedToken, {
                            path: '/',
                            httpOnly: true,
                            sameSite: 'strict',
                            secure: true,
                        });
                        response.cookies.set('appSession.2', encryptedRefreshToken, {
                            path: '/',
                            httpOnly: true,
                            sameSite: 'strict',
                            secure: true,
                        });
                        let currentUser: User = await getUserDetails(payload);
                        const encryptedRole = CryptoJS.AES.encrypt(currentUser.role, secretKey).toString();
                        const encryptedUserFullName = CryptoJS.AES.encrypt(currentUser.name, secretKey).toString();
                        response.cookies.set('appSession.1', encryptedRole, {
                            path: '/',
                            httpOnly: true,
                            sameSite: 'strict',
                            secure: true,
                        });
                        response.cookies.set('appSession.6', encryptedUserFullName, {
                            path: '/',
                            httpOnly: true,
                            sameSite: 'strict',
                            secure: true,
                        });
                        console.log("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG -2");
                        const defaultOrigin = 'https://aps3.zemenbank.com';
                        const defaultDomain = 'aps3.zemenbank.com';
                        const origin = request.headers.get('origin') ?? '';
                        if (origin === defaultOrigin) {
                            response.headers.set('Access-Control-Allow-Origin', defaultDomain);
                            response.headers.set('x-forwarded-host', defaultDomain);
                            response.headers.set('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
                            response.headers.set('Access-Control-Allow-Headers', 'Content-Type, Authorization');
                        }
                        return response;
                    }
                }
                else {
                    console.log("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG -5");
                    request.cookies.clear();
                    const loginUrl = new URL(authURI + '/auth/realms/test/protocol/openid-connect/auth', request.url);
                    loginUrl.searchParams.set('client_id', "TestUDMS");
                    // loginUrl.searchParams.set('client_id', "UDMS");
                    loginUrl.searchParams.set('redirect_uri', redirectURIApp);
                    loginUrl.searchParams.set('response_type', "code");
                    loginUrl.searchParams.set('scope', "openid offline_access");
                    return NextResponse.redirect(loginUrl);
                }
            }
        } catch (error) {
            console.log("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG -6");
            console.log("ERROR === " + error);
            request.cookies.clear();
            const loginUrl = new URL(authURI + '/auth/realms/test/protocol/openid-connect/auth', request.url);
            loginUrl.searchParams.set('client_id', "TestUDMS");
            // loginUrl.searchParams.set('client_id', "UDMS");
            loginUrl.searchParams.set('redirect_uri', redirectURIApp);
            loginUrl.searchParams.set('response_type', "code");
            loginUrl.searchParams.set('scope', "openid offline_access");
            return NextResponse.redirect(loginUrl);
        }
    }
}

