import GoogleIcon from "../icons/google-icon";
import MicrosoftIcon from "../icons/microsoft-icon";
import { Button } from "../ui/button";

const SocialLoginButtons = () => {
    return (
        <div className="space-y-3">
            <div className="flex items-center">
                <span className="w-full border-t" />
                <div className="shrink-0 text-xs uppercase">
                    <span className="text-muted-foreground px-2">
                        Or continue with
                    </span>
                </div>
                <span className="w-full border-t" />
            </div>

            <div className="grid grid-cols-2 gap-3">
                <Button variant="outline">
                    <GoogleIcon /> Google
                </Button>
                <Button variant="outline">
                    <MicrosoftIcon /> Microsoft
                </Button>
            </div>
        </div>
    );
};

export default SocialLoginButtons;
