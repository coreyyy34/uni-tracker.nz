import ResponsiveLogo from "@/components/responsive-logo";
import { FC, PropsWithChildren } from "react";

const AuthLayout: FC<PropsWithChildren> = ({ children }) => {
    return (
        <div className="bg-page flex flex-grow items-center justify-center">
            <div className="flex min-h-128 w-full max-w-4xl flex-col overflow-hidden rounded-xl shadow-2xl lg:flex-row">
                <div className="flex items-center justify-center bg-gradient-to-br from-cyan-600 to-cyan-800 p-8 lg:min-h-full lg:w-1/2">
                    <ResponsiveLogo />
                </div>

                <div className="flex w-full flex-col items-center justify-center bg-white p-8 text-neutral-200 lg:w-1/2 dark:bg-neutral-900">
                    <div className="flex w-full max-w-md flex-col space-y-6">
                        {children}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AuthLayout;
