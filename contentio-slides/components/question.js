import React, { Component } from "react";
import { Box, Flex, Text } from "rebass";
import Statement from "./Statement";
import IBMText from "./IBMText";
import { kNumber, timeAgo } from "../utils/formatterUtils";

const Question = ({ submission, sentences, slide }) => {
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
			<Flex>
				<Box mt={3} mr={4} style={{ textAlign: "center" }}>
					<Text
						fontFamily="RedditFont"
						fontWeight="normal"
						fontSize={6}
						color="orange"
					>
						&#61755;
					</Text>
					<IBMText color="orange" fontWeight="bold" fontSize={5}>
						{kNumber(submission.score)}
					</IBMText>
					<Text
						fontFamily="RedditFont"
						fontWeight="normal"
						fontSize={6}
						color="gray"
					>
						&#61712;
					</Text>
				</Box>
				<Flex flexDirection="column">
					<Box my={1}>
						<IBMText color="gray" fontSize={5}>
							Posted by u/{submission.author} {timeAgo(created)}
						</IBMText>
					</Box>
					<Box>
						<Statement
							sentences={sentences}
							slide={slide}
							fontFamily="IBM Plex Sans"
							fontSize={6}
							fontWeight="bold"
						/>
					</Box>
					<Box mt={2}>
						<Flex>
							<Text
								mt={1}
								mr={2}
								fontFamily="RedditFont"
								fontWeight="normal"
								fontSize={5}
								color={bottomColor}
							>
								&#61708;
							</Text>
							<IBMText
								mr={3}
								fontWeight="bold"
								fontSize={5}
								color={bottomColor}
							>
								Comments
							</IBMText>
							<Text
								mt={1}
								mr={2}
								fontFamily="RedditFont"
								fontWeight="normal"
								fontSize={5}
								color={bottomColor}
							>
								&#61748;
							</Text>
							<IBMText
								mr={3}
								fontWeight="bold"
								fontSize={5}
								color={bottomColor}
							>
								Share
							</IBMText>
							<Text
								mt={1}
								mr={2}
								fontFamily="RedditFont"
								fontWeight="normal"
								fontSize={5}
								color={bottomColor}
							>
								&#61747;
							</Text>
							<IBMText
								fontWeight="bold"
								fontSize={5}
								color={bottomColor}
							>
								Save
							</IBMText>
						</Flex>
					</Box>
				</Flex>
			</Flex>
		</Flex>
	);
};

export default Question;
