import React from "react";
import styled from "styled-components";
import { Button } from "rebass";

export const PrimaryButton = styled(Button)`
	border-style: solid;
	border-color: #005005;
	border-width: 2px;
	cursor: pointer;
	background: rgb(76, 175, 80);
	background: linear-gradient(
		180deg,
		rgba(76, 175, 80, 1) 0%,
		rgba(8, 127, 35, 1) 100%
	);
	&:hover {
		border-color: #003300;
		background: rgb(67, 160, 71);
		background: linear-gradient(
			180deg,
			rgba(67, 160, 71, 1) 0%,
			rgba(0, 112, 26, 1) 100%
		);
	}
`;
