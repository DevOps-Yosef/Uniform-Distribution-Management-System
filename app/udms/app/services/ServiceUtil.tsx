"use server";
import { cookies } from 'next/headers'
import CryptoJS from 'crypto-js';
import { NextResponse } from "next/server";

// const redirectURIApp = "http://localhost:8083";
const redirectURIApp = "https://aps3.zemenbank.com/TestUDMS";
// const redirectURIApp = "https://aps3.zemenbank.com/UDMS";
// const authURI = "https://auth.zemenbank.com";
const authURI = "https://aps3.zemenbank.com/TestUDMS";
// const authURI = "https://aps3.zemenbank.com/UDMS";

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

export async function refreshInactiveToken() {
  try {
    console.log("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC8CCCCCCCCCCCCCCCCCCCCCCC-1");
    const secretKey = process.env.SECRET_KEY;
    const refreshCookie = cookies().get('appSession.2')?.value;
    if (refreshCookie) {
      console.log("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC-2");
      const bytes = CryptoJS.AES.decrypt(refreshCookie, secretKey);
      console.log("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC-3");
      const refreshSession = bytes.toString(CryptoJS.enc.Utf8);
      console.log("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC-4");
      if (refreshSession) {
        console.log("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC-5");
        const tokenDataObject = await refreshToken(refreshSession);
        if (tokenDataObject && tokenDataObject.active) {
          console.log("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC-6");
          const payload = tokenDataObject.token;
          const refreskTokenPayload = tokenDataObject.refreshToken;
          const encryptedToken = CryptoJS.AES.encrypt(payload, secretKey).toString();
          const encryptedRefreshToken = CryptoJS.AES.encrypt(refreskTokenPayload, secretKey).toString();
          cookies().delete('appSession.2');
          cookies().delete('appSession.4');
          const encryptedRole = cookies().get('appSession.1')?.value;
          const encryptedUserFullName = cookies().get('appSession.6')?.value;
          cookies().delete('appSession.1');
          cookies().delete('appSession.6');
          console.log("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC-7");
          cookies().set('appSession.4', encryptedToken, {
            path: '/',
            httpOnly: true,
            sameSite: 'strict',
            secure: true,
          });
          cookies().set('appSession.2', encryptedRefreshToken, {
            path: '/',
            httpOnly: true,
            sameSite: 'strict',
            secure: true,
          });
          cookies().set('appSession.1', encryptedRole, {
            path: '/',
            httpOnly: true,
            sameSite: 'strict',
            secure: true,
          });
          cookies().set('appSession.6', encryptedUserFullName, {
            path: '/',
            httpOnly: true,
            sameSite: 'strict',
            secure: true,
          });
          console.log("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC-8");
        }
      }
    }
  } catch (error) {
    console.log("REFRESH TOKEN ERROR === " + error);
  }
}

export async function requestResourceWithCredsPost(apiURL: string, queryParams: any) {
  const loginUrl = new URL('/auth/realms/test/protocol/openid-connect/auth', authURI);
  loginUrl.searchParams.set('client_id', "TestUDMS");
  // loginUrl.searchParams.set('client_id', "UDMS");
  loginUrl.searchParams.set('redirect_uri', redirectURIApp);
  loginUrl.searchParams.set('response_type', "code");
  loginUrl.searchParams.set('scope', "openid offline_access");
  try {
    const cookie = cookies().get('appSession.4')?.value;
    const secretKey = process.env.SECRET_KEY;
    if (cookie) {
      const bytes = CryptoJS.AES.decrypt(cookie, secretKey);
      const accessToken = bytes.toString(CryptoJS.enc.Utf8);
      if (accessToken) {
        const options: RequestInit = {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`
          },
          body: JSON.stringify(queryParams),
          cache: 'no-store'
        };
        const response = await fetch(apiURL, options);
        if (response.ok) {
          const data = await response.json();
          return data;
        }
        else if (response.status === 403) {
          return {
            status: 403,
            message: "Unauthorized access",
          };
        }
      }
    }
  }
  catch (error) {
    console.log(error);
  }
  return NextResponse.redirect(loginUrl);
}

export const requestReports = async (apiURL: string, queryParams: any) => {
  try {
    const cookie = cookies().get('appSession.4')?.value;
    const secretKey = process.env.SECRET_KEY;
    if (cookie) {
      const bytes = CryptoJS.AES.decrypt(cookie, secretKey);
      const accessToken = bytes.toString(CryptoJS.enc.Utf8);
      if (accessToken) {
        const options: RequestInit = {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`
          },
          body: JSON.stringify(queryParams),
          cache: 'no-store'
        };
        const response = await fetch(apiURL, options);
        if (response.ok) {
          const ab = await response.arrayBuffer();
          let object = {
            byteArr: Array.from(new Uint8Array(ab)),
          };
          return object;
        } 
      }
    }
  }
  catch (error) {
    console.log(error);
  }
};

export async function getUserDetailsOne() {
  let roleName = "";
  try {
    const encryptedRole = cookies().get('appSession.1')?.value;
    const secretKey = process.env.SECRET_KEY;
    if (encryptedRole) {
      const bytes = CryptoJS.AES.decrypt(encryptedRole, secretKey);
      roleName = bytes.toString(CryptoJS.enc.Utf8);
    }
  } catch (error) {
  }
  return roleName;
}

export async function getUserDetailsTwo() {
  let userFullName = "";
  try {
    const encryptedUserFullName = cookies().get('appSession.6')?.value;
    const secretKey = process.env.SECRET_KEY;
    if (encryptedUserFullName) {
      const bytes = CryptoJS.AES.decrypt(encryptedUserFullName, secretKey);
      userFullName = bytes.toString(CryptoJS.enc.Utf8);
    }
  } catch (error) {
  }
  return userFullName;
}