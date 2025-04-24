import React from 'react';

export function OnboardingSurvey(){
  return (
    <div className="onboarding-survey">
      <h2>Welcome to Draft!</h2>
      <p>We're excited to have you onboard! Please complete the following steps:</p>

      <div className="survey-step">
        <h3>Step 1: Tell us about your style</h3>
        <select>
          <option>Casual</option>
          <option>Vintage</option>
          <option>Streetwear</option>
        </select>
      </div>

      <div className="survey-step">
        <h3>Step 2: How often do you shop for secondhand fashion?</h3>
        <input type="radio" id="frequent" name="shopping-frequency" value="frequent" />
        <label htmlFor="frequent">Frequently</label>
        <input type="radio" id="occasionally" name="shopping-frequency" value="occasionally" />
        <label htmlFor="occasionally">Occasionally</label>
        <input type="radio" id="never" name="shopping-frequency" value="never" />
        <label htmlFor="never">Never</label>
      </div>

      <button>Complete Onboarding</button>
    </div>
  );
};