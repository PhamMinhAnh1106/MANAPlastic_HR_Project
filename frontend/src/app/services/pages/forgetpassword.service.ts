import { api } from "../api.service";

export async function getOtpByEmail(email: string) {
    try {
        const res = await api.post("/user/authOtp/forgot-password", { email });
        return {
            data: res.data,
            status: res.status
        }
    } catch (error) {
        return error;
    }

}

export async function checkOtpByEmail(email: string, otp: string) {
    try {
        const res = await api.post("/user/authOtp/verify-otp", { email, otp });
        return {
            data: res.data,
            status: res.status
        }
    } catch (error) {
        return error;
    }

}
export async function resetNewPassword(resetToken: string, newPassword: string) {
    try {
        const res = await api.post("/user/authOtp/reset-password", { resetToken, newPassword });
        return {
            data: res.data,
            status: res.status
        }
    } catch (error) {
        return error;
    }

}
