import React, { Component } from "react";
import { Box, Flex, Card, Text, Heading, Image } from "rebass";

export default class Thumbnail extends Component {
	render() {
		const { src, width, resolution } = this.props;
		const height = width / resolution;
		return (
			<Box
				bg="background2"
				style={{ minWidth: width, maxWidth: width, height }}
			>
				<Image src={src} />
			</Box>
		);
	}
}
