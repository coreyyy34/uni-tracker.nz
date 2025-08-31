import Image from "next/image";

const ResponsiveLogo = () => {
    const sharedClasses =
        "h-auto w-full drop-shadow-[0_0_10px_rgba(255, 255, 255, 0.3) filter";

    return (
        <div>
            <Image
                src="/logo-long.svg"
                height={0}
                width={0}
                className={`${sharedClasses} block max-w-lg lg:hidden`}
                alt="Uni-Tracker Logo"
            ></Image>
            <Image
                src="/logo.svg"
                height={0}
                width={0}
                className={`${sharedClasses} hidden max-w-xs lg:block`}
                alt="Uni-Tracker Logo"
            ></Image>
        </div>
    );
};

export default ResponsiveLogo;
