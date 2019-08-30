import React from "react";
import styled from "styled-components";
import theme from "../utils/theme";
import { Button } from "rebass";

// material design colors https://material.io/resources/color/
const colors = {
	green500: "#4caf50",
	green500dark: "#087f23",
	green600: "#43a047",
	green600dark: "#00701a",
	green800dark: "#005005",
	green900dark: "#003300"
};

const BasicButton = styled(Button)`
	cursor: pointer;
	&:focus {
		outline: none;
	}
`;

export const PrimaryButton = styled(BasicButton)`
	border-style: solid;
	border-color: ${colors.green800dark};
	border-width: 2px;
	background: ${colors.green500};
	background: linear-gradient(
		180deg,
		${colors.green500} 0%,
		${colors.green500dark} 100%
	);
	&:hover {
		border-color: ${colors.green900dark};
		background: ${colors.green600};
		background: linear-gradient(
			180deg,
			${colors.green600} 0%,
			${colors.green600dark} 100%
		);
	}
`;

export const OutlineButton = styled(BasicButton)`
	border-style: solid;
	border-color: ${theme.colors.text2};
	border-width: 2px;
	color: white;
	background-color: rgba(0, 0, 0, 0);
	&:hover {
		background-color: #1565c0;
		border-color: #003c8f;
		color: white;
	}
`;
