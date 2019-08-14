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
      <Box my={1}>
        <IBMText color="gray" fontSize={4}>Posted by u/{submission.author}</IBMText>
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
      <Box mt={4}>
        <Flex>
          <IBMText fontWeight="bold" fontSize={4} color={bottomColor}>
            13.4k Comments
          </IBMText>
          <IBMText fontWeight="bold" fontSize={4} color={bottomColor} mx={3}>
            Share
          </IBMText>
          <IBMText fontWeight="bold" fontSize={4} color={bottomColor} >
            Save
          </IBMText>
        </Flex>
      </Box>
    </Flex>
  );
};

export default Question;
