import SocialLoginButtons from "@/components/auth/social-login-buttons";
import Header from "@/components/header";
import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

const LoginPage = () => {
    return (
        <>
            <div className="space-y-2 text-center">
                <Header text="Welcome Back" />

                <p className="text-muted-foreground">
                    Sign in to your account to continue
                </p>
            </div>

            <div className="space-y-6">
                <div className="space-y-4">
                    <div className="space-y-2">
                        <Label htmlFor="email" className="text-foreground">
                            Email address
                        </Label>
                        <Input
                            id="email"
                            type="email"
                            placeholder="Enter your email"
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="password" className="text-foreground">
                            Password
                        </Label>
                        <Input
                            id="password"
                            type="password"
                            placeholder="Enter your password"
                        />
                    </div>

                    <div className="flex items-center justify-between">
                        <div className="flex items-center space-x-2">
                            <Checkbox id="remember" />
                            <Label
                                htmlFor="remember"
                                className="text-muted-foreground"
                            >
                                Remember me
                            </Label>
                        </div>
                        <Button
                            variant="link"
                            className="text-primary hover:text-accent px-0"
                        >
                            Forgot password?
                        </Button>
                    </div>
                </div>

                <Button className="w-full">Sign in</Button>

                <SocialLoginButtons />

                <div className="text-muted-foreground text-center text-sm">
                    Don't have an account?{" "}
                    <Button variant="link" className="px-0">
                        Sign up
                    </Button>
                </div>
            </div>
        </>
    );
};

export default LoginPage;
