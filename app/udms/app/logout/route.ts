import { redirect } from 'next/navigation'
import { cookies } from "next/headers";
import { NextRequest, NextResponse } from 'next/server';

// const redirectURIApp = "http://localhost:8083";
const redirectURIApp = "https://aps3.zemenbank.com/TestUDMS";
// const redirectURIApp = "https://aps3.zemenbank.com/UDMS";
// const authURI = "https://auth.zemenbank.com";
const authURI = "https://aps3.zemenbank.com/TestUDMS";
// const authURI = "https://aps3.zemenbank.com/UDMS";

export async function GET(request: NextRequest) {
    try {
        cookies().delete("appSession.4");
        cookies().delete("appSession.1");
        cookies().delete("appSession.2");
        cookies().delete("appSession.6");
    } catch (error) {
    }
    const clientId = "TestUDMS";
    // const clientId = "UDMS";
    const url = authURI + "/auth/realms/test/protocol/openid-connect/logout?"
        + "client_id=" + clientId + "&post_logout_redirect_uri=" + redirectURIApp;
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
    redirect(url);
}


