import React, { Component } from "react";
import { Box, Flex, Text } from "rebass";
import Statement from "./Statement";
import IBMText from "./IBMText";
import { kNumber, timeAgo } from "../utils/formatterUtils";

const Answer = ({ submission, sentences, slide, fontSize }) => {
	const created = new Date(submission.created);
	const showBottomBar = slide >= sentences.length - 1;
	const bottomColor = showBottomBar ? "gray" : "black";

	return (
		<Flex
			p={5}
			bg="black"
			flexDirection="column"
			justifyContent="center"
			style={{ height: "100vh" }}
		>
			<Box>
				<Flex my={1}>
					<IBMText color="blue" fontSize={fontSize}>
						{submission.author}
					</IBMText>
					<IBMText mx={2} color="gray" fontSize={fontSize}>
						{kNumber(submission.score)} points
					</IBMText>
					<IBMText color="gray" fontSize={fontSize}>
						•
					</IBMText>
					<IBMText mx={2} color="gray" fontSize={fontSize}>
						{timeAgo(created)}
					</IBMText>
				</Flex>
			</Box>

			<Box>
				<Statement
					sentences={sentences}
					slide={slide}
					fontFamily="Noto Sans"
					fontSize={fontSize}
				/>
			</Box>

			<Box my={1}>
				<Flex>
					<Text
						mt={1}
						mr={2}
						fontFamily="RedditFont"
						fontWeight="normal"
						fontSize={fontSize}
						color={bottomColor}
					>
						&#61708;
					</Text>
					<IBMText
						mr={3}
						fontWeight="bold"
						color={bottomColor}
						fontSize={fontSize}
					>
						Reply
					</IBMText>
					<Text
						mt={1}
						mr={2}
						fontFamily="RedditFont"
						fontWeight="normal"
						fontSize={fontSize}
						color={bottomColor}
					>
						&#61748;
					</Text>
					<IBMText
						mr={3}
						fontWeight="bold"
						color={bottomColor}
						fontSize={fontSize}
					>
						Share
					</IBMText>
					<Text
						mt={1}
						mr={2}
						fontFamily="RedditFont"
						fontWeight="normal"
						fontSize={fontSize}
						color={bottomColor}
					>
						&#61747;
					</Text>
					<IBMText
						fontWeight="bold"
						color={bottomColor}
						fontSize={fontSize}
					>
						Save
					</IBMText>
				</Flex>
			</Box>
		</Flex>
	);
};

export default Answer;
