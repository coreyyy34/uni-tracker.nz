import SocialLoginButtons from "@/components/auth/social-login-buttons";
import Header from "@/components/header";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

const RegisterPage = () => {
    return (
        <>
            <div className="space-y-2 text-center">
                <Header text="Create Account" />

                <p className="text-muted-foreground">Join us and get started</p>
            </div>

            <div className="space-y-6">
                <div className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-2">
                            <Label
                                htmlFor="firstName"
                                className="text-foreground"
                            >
                                First Name
                            </Label>
                            <Input
                                id="firstName"
                                name="firstName"
                                type="text"
                                placeholder="John"
                            />
                        </div>
                        <div className="space-y-2">
                            <Label
                                htmlFor="lastName"
                                className="text-foreground"
                            >
                                Last Name
                            </Label>
                            <Input
                                id="lastName"
                                name="lastName"
                                type="text"
                                placeholder="Doe"
                            />
                        </div>
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="email" className="text-foreground">
                            Email
                        </Label>
                        <Input
                            id="email"
                            name="email"
                            type="email"
                            placeholder="john@example.com"
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="password" className="text-foreground">
                            Password
                        </Label>
                        <Input
                            id="password"
                            name="password"
                            type="password"
                            placeholder="Create a strong password"
                        />
                    </div>

                    <div className="space-y-2">
                        <Label
                            htmlFor="confirmPassword"
                            className="text-foreground"
                        >
                            Confirm Password
                        </Label>
                        <Input
                            id="confirmPassword"
                            name="confirmPassword"
                            type="password"
                            placeholder="Confirm your password"
                        />
                    </div>
                </div>

                <Button className="w-full">Create Account</Button>

                <SocialLoginButtons />

                <div className="text-muted-foreground text-center text-sm">
                    Already have an account?{" "}
                    <Button variant="link" className="px-0">
                        Sign in
                    </Button>
                </div>
            </div>
        </>
    );
};

export default RegisterPage;
