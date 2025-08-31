import { FC } from "react";

interface HeaderProps {
    text: string;
}

const Header: FC<HeaderProps> = ({ text }) => {
    return <h1 className="text-foreground text-3xl font-bold">{text}</h1>;
};

export default Header;
