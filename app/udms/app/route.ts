import { redirect } from 'next/navigation';
import { NextRequest, NextResponse } from 'next/server';
import { cookies } from 'next/headers';
import CryptoJS from 'crypto-js';

// const redirectURIApp = "http://localhost:8083";
const redirectURIApp = "https://aps3.zemenbank.com/TestUDMS";
// const redirectURIApp = "https://aps3.zemenbank.com/UDMS";
// const authURI = "https://auth.zemenbank.com";
const authURI = "https://aps3.zemenbank.com/TestUDMS";
// const authURI = "https://aps3.zemenbank.com/UDMS";
// const homeURI = "/home";
const homeURI = "/TestUDMS/home";
// const homeURI = "/UDMS/home";



async function validateToken(token) {
    let status = false;
    const options: RequestInit = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            Authorization: `Bearer ${token}`
        },
        body: new URLSearchParams({
            token: token,
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
    console.log("Validate Token Route === " + status);
    return status;
}

export async function GET(request: NextRequest) {
    console.log("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT -1");
    // const cookie = cookies().get('appSession.4')?.value;
    const cookie = request.cookies.get('appSession.4')?.value;
    const loginUrl = new URL(authURI + '/auth/realms/test/protocol/openid-connect/auth', request.url);
    loginUrl.searchParams.set('client_id', "TestUDMS");
    // loginUrl.searchParams.set('client_id', "UDMS");
    loginUrl.searchParams.set('redirect_uri', redirectURIApp);
    loginUrl.searchParams.set('response_type', "code");
    loginUrl.searchParams.set('scope', "openid offline_access");
    console.log("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT -2");
    if (cookie) {
        console.log("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT -3");
        const secretKey = process.env.SECRET_KEY;
        const bytes = CryptoJS.AES.decrypt(cookie, secretKey);
        const session = bytes.toString(CryptoJS.enc.Utf8);
        if (!session) {
            console.log("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT -4");
            return NextResponse.redirect(loginUrl);
        }
        else {
            console.log("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT -5");
            const tokenStatus = await validateToken(session);
            if (!tokenStatus) {
                console.log("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT -6");
                request.cookies.delete('appSession.4');
                request.cookies.delete('appSession.1');
                request.cookies.delete('appSession.6');
                return NextResponse.redirect(loginUrl);
            }
            else {
                console.log("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT -7");
                redirect(homeURI);
            }
        }
    }
    console.log("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT -8");
    return NextResponse.redirect(loginUrl);
}