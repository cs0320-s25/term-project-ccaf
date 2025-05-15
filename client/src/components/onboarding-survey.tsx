"use client";

import { useState } from "react";

interface OnboardingSurveyProps {
  onComplete: () => void;
}

export function OnboardingSurvey({ onComplete }: OnboardingSurveyProps) {
  const steps = [
    {
      title: "welcome to draft!",
      description: "answer a few questions to help us find the right drafts for you.",
      type: "intro",
    },
    {
      title: "how would you describe your style?",
      description: "this helps us find relevant search results",
      options: ["casual", "vintage", "streetwear", "minimalist", "other"],
      type: "select",
    },
    {
      title: "how often do you shop secondhand?",
      description: "help us understand how draft can support u!",
      options: ["frequently", "occasionally", "rarely", "never"],
      type: "select",
    },
    {
      title: "done!",
      description: "ready to meet your drafts?",
      type: "outro",
    },
  ];

  const [currentStep, setCurrentStep] = useState(0);
  const [responses, setResponses] = useState<{ [key: string]: string }>({});

  const handleNext = () => {
    if (currentStep === steps.length - 1) {
      onComplete();
    } else {
      setCurrentStep((prev) => prev + 1);
    }
  };

  const handleBack = () => {
    if (currentStep > 0) {
      setCurrentStep((prev) => prev - 1);
    }
  };

  const handleSelect = (option: string) => {
    const key = steps[currentStep].title;
    setResponses((prev) => ({ ...prev, [key]: option }));
    handleNext();
  };

  const progress = ((currentStep + 1) / steps.length) * 100;

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black/40 z-50">
      <div className="bg-white rounded-lg shadow-lg w-full max-w-md p-8 relative">
        
        {/* progress bar for steps */}
        <div className="h-2 bg-gray-200 rounded-full mb-6 overflow-hidden">
          <div
            className="h-full bg-black transition-all"
            style={{ width: `${progress}%` }}
          />
        </div>

        {/* text content styling */}
        <h2 className="mb-4">{steps[currentStep].title}</h2>
        <p className=" mb-6">{steps[currentStep].description}</p>

        {/* options */}
        {steps[currentStep].type === "select" && (
          <div className="flex flex-col gap-2">
            {steps[currentStep].options?.map((option) => (
              <button
                key={option}
                className="btn-outline-rounded w-full"
                onClick={() => handleSelect(option)}
              >
                {option}
              </button>
            ))}
          </div>
        )}

        {/* buttons for start/end screens */}
        {["intro", "outro"].includes(steps[currentStep].type) && (
          <div className="flex justify-center gap-4 mt-6">
            {currentStep > 0 && (
              <button className="btn-outline-rounded" onClick={handleBack}>
                go back
              </button>
            )}
            <button className="btn-outline-rounded" onClick={handleNext}>
              {currentStep === steps.length - 1 ? "take me there!" : "Next"}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
